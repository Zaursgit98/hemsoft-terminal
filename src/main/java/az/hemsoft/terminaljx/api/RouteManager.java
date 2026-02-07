package az.hemsoft.terminaljx.api;

import az.hemsoft.terminaljx.business.core.annotation.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class RouteManager {

    public static void registerControllers(Javalin app, Object... controllers) {
        for (Object controller : controllers) {
            registerController(app, controller);
        }
    }

    private static void registerController(Javalin app, Object controller) {
        Class<?> clazz = controller.getClass();
        if (!clazz.isAnnotationPresent(RestController.class))
            return;

        String baseUrl = clazz.getAnnotation(RestController.class).value();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                String path = baseUrl + method.getAnnotation(GetMapping.class).value();
                app.get(path, ctx -> handleRequest(controller, method, ctx));
            } else if (method.isAnnotationPresent(PostMapping.class)) {
                String path = baseUrl + method.getAnnotation(PostMapping.class).value();
                app.post(path, ctx -> handleRequest(controller, method, ctx));
            } else if (method.isAnnotationPresent(PutMapping.class)) {
                String path = baseUrl + method.getAnnotation(PutMapping.class).value();
                app.put(path, ctx -> handleRequest(controller, method, ctx));
            } else if (method.isAnnotationPresent(DeleteMapping.class)) {
                String path = baseUrl + method.getAnnotation(DeleteMapping.class).value();
                app.delete(path, ctx -> handleRequest(controller, method, ctx));
            }
        }
    }

    private static void handleRequest(Object controller, Method method, Context ctx) {
        try {
            Parameter[] parameters = method.getParameters();
            Object[] args = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                Parameter p = parameters[i];
                if (p.getType() == Context.class) {
                    args[i] = ctx;
                } else if (p.isAnnotationPresent(PathVariable.class)) {
                    String paramName = p.getAnnotation(PathVariable.class).value();
                    if (paramName.isEmpty())
                        paramName = p.getName();
                    String val = ctx.pathParam(paramName);
                    args[i] = cast(val, p.getType());
                } else if (p.isAnnotationPresent(RequestBody.class)) {
                    args[i] = ctx.bodyAsClass(p.getType());
                }
            }

            Object result = method.invoke(controller, args);
            if (result != null && !(result instanceof Void)) {
                ctx.json(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
        }
    }

    private static Object cast(String val, Class<?> targetType) {
        if (targetType == Integer.class || targetType == int.class)
            return Integer.parseInt(val);
        if (targetType == Long.class || targetType == long.class)
            return Long.parseLong(val);
        if (targetType == Double.class || targetType == double.class)
            return Double.parseDouble(val);
        if (targetType == Boolean.class || targetType == boolean.class)
            return Boolean.parseBoolean(val);
        return val;
    }
}
