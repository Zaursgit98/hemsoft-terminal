package az.hemsoft.terminaljx.business.core.repository;

import az.hemsoft.terminaljx.business.core.annotation.*;
import az.hemsoft.terminaljx.business.core.mapper.RowMapper;
import az.hemsoft.terminaljx.config.DatabaseManager;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class BaseRepository<T, ID> {
    protected final Connection connection;
    protected final String tableName;
    protected final String idColumn;
    protected final Class<T> entityClass;

    private static final Map<Class<?>, List<FieldMetadata>> metadataCache = new ConcurrentHashMap<>();

    private static class FieldMetadata {
        Field field;
        String columnName;
        boolean isId;
        boolean isRelation;
        boolean isCollection;
        FetchType fetchType;

        FieldMetadata(Field field, String columnName, boolean isId, boolean isRelation, boolean isCollection,
                FetchType fetchType) {
            this.field = field;
            this.columnName = columnName;
            this.isId = isId;
            this.isRelation = isRelation;
            this.isCollection = isCollection;
            this.fetchType = fetchType;
            this.field.setAccessible(true);
        }
    }

    protected BaseRepository() {
        this.entityClass = detectEntityClass();
        this.connection = DatabaseManager.getInstance().getConnection();
        this.tableName = detectTableName();
        this.idColumn = detectIdColumn();
        initializeMetadata(entityClass);
    }

    @SuppressWarnings("unchecked")
    private Class<T> detectEntityClass() {
        java.lang.reflect.Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof java.lang.reflect.ParameterizedType) {
            return (Class<T>) ((java.lang.reflect.ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        }
        throw new RuntimeException("Generic type T could not be determined for " + getClass().getName());
    }

    private String detectTableName() {
        Table tableAnn = entityClass.getAnnotation(Table.class);
        return (tableAnn != null) ? tableAnn.value() : entityClass.getSimpleName().toLowerCase();
    }

    private String detectIdColumn() {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                if (field.isAnnotationPresent(Column.class)) {
                    String name = field.getAnnotation(Column.class).name();
                    if (!name.isEmpty())
                        return name;
                }
                return field.getName();
            }
        }
        return "id";
    }

    private void initializeMetadata(Class<?> clazz) {
        metadataCache.computeIfAbsent(clazz, c -> {
            List<FieldMetadata> meta = new ArrayList<>();
            for (Field field : c.getDeclaredFields()) {
                boolean isId = field.isAnnotationPresent(Id.class);
                boolean isManyToOne = field.isAnnotationPresent(ManyToOne.class);
                boolean isOneToOne = field.isAnnotationPresent(OneToOne.class);
                boolean isOneToMany = field.isAnnotationPresent(OneToMany.class);
                boolean isManyToMany = field.isAnnotationPresent(ManyToMany.class);

                if (!isId && !isManyToOne && !isOneToOne && !isOneToMany && !isManyToMany
                        && !field.isAnnotationPresent(Column.class)) {
                    continue;
                }

                boolean isRelation = isManyToOne || isOneToOne;
                boolean isCollection = isOneToMany || isManyToMany;
                FetchType fetchType = FetchType.EAGER;
                String colName = field.getName();

                if (isManyToOne) {
                    fetchType = field.getAnnotation(ManyToOne.class).fetch();
                } else if (isOneToOne) {
                    fetchType = field.getAnnotation(OneToOne.class).fetch();
                } else if (isOneToMany) {
                    fetchType = field.getAnnotation(OneToMany.class).fetch();
                } else if (isManyToMany) {
                    fetchType = field.getAnnotation(ManyToMany.class).fetch();
                }

                if (field.isAnnotationPresent(JoinColumn.class)) {
                    colName = field.getAnnotation(JoinColumn.class).name();
                } else if (field.isAnnotationPresent(Column.class)) {
                    Column annotation = field.getAnnotation(Column.class);
                    if (!annotation.name().isEmpty())
                        colName = annotation.name();
                }

                meta.add(new FieldMetadata(field, colName, isId, isRelation, isCollection, fetchType));
            }
            return meta;
        });
    }

    protected Map<String, Object> mapToColumns(T item) {
        Map<String, Object> columns = new LinkedHashMap<>();
        for (FieldMetadata meta : metadataCache.get(entityClass)) {
            if (meta.isId || meta.isCollection)
                continue;
            try {
                Object value = meta.field.get(item);
                if (meta.isRelation && value != null) {
                    Field idF = value.getClass().getDeclaredField("id");
                    idF.setAccessible(true);
                    value = idF.get(value);
                }
                columns.put(meta.columnName, value);
            } catch (Exception e) {
                throw new RuntimeException("Error mapping field: " + meta.field.getName(), e);
            }
        }
        return columns;
    }

    protected RowMapper<T> getRowMapper() {
        return rs -> {
            try {
                return mapEntity(rs, entityClass);
            } catch (Exception e) {
                throw new SQLException("Error mapping entity: " + entityClass.getSimpleName(), e);
            }
        };
    }

    private <E> E mapEntity(ResultSet rs, Class<E> clazz) throws Exception {
        E item = clazz.getDeclaredConstructor().newInstance();
        initializeMetadata(clazz);

        for (FieldMetadata meta : metadataCache.get(clazz)) {
            Object value = rs.getObject(meta.columnName);
            if (value == null)
                continue;

            if (meta.isRelation) {
                Class<?> relClass = meta.field.getType();
                Object relId = cast(value, Integer.class);
                if (relId != null) {
                    if (meta.fetchType == FetchType.LAZY) {
                        // Shallow load for LAZY
                        Object relObj = relClass.getDeclaredConstructor().newInstance();
                        Field idF = relClass.getDeclaredField("id");
                        idF.setAccessible(true);
                        idF.set(relObj, relId);
                        meta.field.set(item, relObj);
                    } else {
                        // Deep load for EAGER
                        meta.field.set(item, fetchRelation(relClass, relId));
                    }
                }
            } else {
                meta.field.set(item, cast(value, meta.field.getType()));
            }
        }
        return item;
    }

    private Object fetchRelation(Class<?> relClass, Object id) {
        initializeMetadata(relClass);
        Table tableAnn = relClass.getAnnotation(Table.class);
        String relTableName = (tableAnn != null) ? tableAnn.value() : relClass.getSimpleName().toLowerCase();
        String sql = "SELECT * FROM " + relTableName + " WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Object relObj = relClass.getDeclaredConstructor().newInstance();
                    for (FieldMetadata meta : metadataCache.get(relClass)) {
                        Object value = rs.getObject(meta.columnName);
                        if (value != null) {
                            if (meta.isRelation) {
                                // Simple shallow load for secondary relations to avoid infinite loops
                                Object subRel = meta.field.getType().getDeclaredConstructor().newInstance();
                                Field idF = meta.field.getType().getDeclaredField("id");
                                idF.setAccessible(true);
                                idF.set(subRel, cast(value, idF.getType()));
                                meta.field.set(relObj, subRel);
                            } else {
                                meta.field.set(relObj, cast(value, meta.field.getType()));
                            }
                        }
                    }
                    return relObj;
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: Failed to fetch relation " + relClass.getSimpleName() + " with id " + id);
        }
        return null;
    }

    private Object cast(Object value, Class<?> targetType) {
        if (value == null)
            return null;
        if (targetType.isAssignableFrom(value.getClass()))
            return value;
        if (targetType == Integer.class || targetType == int.class)
            return ((Number) value).intValue();
        if (targetType == Long.class || targetType == long.class)
            return ((Number) value).longValue();
        if (targetType == Double.class || targetType == double.class)
            return ((Number) value).doubleValue();
        return value;
    }

    public List<T> findAll() {
        String sql = "SELECT * FROM " + tableName;
        return query(sql, getRowMapper());
    }

    public Optional<T> findById(ID id) {
        String sql = String.format("SELECT * FROM %s WHERE %s = ?", tableName, idColumn);
        return queryUnique(sql, getRowMapper(), id);
    }

    @SuppressWarnings("unchecked")
    public T save(T item) {
        Map<String, Object> columns = mapToColumns(item);
        if (columns.isEmpty())
            return item;

        String colNames = String.join(", ", columns.keySet());
        String placeholders = columns.keySet().stream().map(k -> "?").collect(Collectors.joining(", "));
        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, colNames, placeholders);

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 1;
            for (Object value : columns.values())
                pstmt.setObject(i++, value);
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next())
                    setId(item, (ID) generatedKeys.getObject(1));
            }
        } catch (SQLException e) {
            handleException(e);
        }
        return item;
    }

    public void update(T item) {
        ID id = getId(item);
        if (id == null)
            return;
        Map<String, Object> columns = mapToColumns(item);
        if (columns.isEmpty())
            return;

        String setClause = columns.keySet().stream().map(col -> col + " = ?").collect(Collectors.joining(", "));
        String sql = String.format("UPDATE %s SET %s WHERE %s = ?", tableName, setClause, idColumn);

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            int i = 1;
            for (Object value : columns.values())
                pstmt.setObject(i++, value);
            pstmt.setObject(i, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    public void delete(ID id) {
        String sql = String.format("DELETE FROM %s WHERE %s = ?", tableName, idColumn);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    protected List<T> query(String sql, RowMapper<T> mapper, Object... params) {
        List<T> results = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++)
                pstmt.setObject(i + 1, params[i]);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next())
                    results.add(mapper.mapRow(rs));
            }
        } catch (SQLException e) {
            handleException(e);
        }
        return results;
    }

    protected Optional<T> queryUnique(String sql, RowMapper<T> mapper, Object... params) {
        List<T> results = query(sql, mapper, params);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    protected void handleException(SQLException e) {
        System.err.println("\u274C SQL Error in " + tableName + ": " + e.getMessage());
        throw new RuntimeException(e);
    }

    protected void setId(T item, ID id) {
        try {
            Field f = entityClass.getDeclaredField("id");
            f.setAccessible(true);
            f.set(item, id);
        } catch (Exception e) {
            // If field 'id' doesn't exist or other error, just log and continue
        }
    }

    @SuppressWarnings("unchecked")
    protected ID getId(T item) {
        try {
            Field f = entityClass.getDeclaredField("id");
            f.setAccessible(true);
            return (ID) f.get(item);
        } catch (Exception e) {
            return null;
        }
    }
}
