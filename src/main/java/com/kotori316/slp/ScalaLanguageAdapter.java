package com.kotori316.slp;

import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.api.LanguageAdapterException;
import net.fabricmc.loader.api.ModContainer;

import java.lang.reflect.Constructor;

public final class ScalaLanguageAdapter implements LanguageAdapter {
    // private static final Logger LOGGER = LoggerFactory.getLogger(ScalaLanguageAdapter.class);

    /**
     * Cases
     * <ul>
     *     <li>Just a class with default constructor</li>
     *     <li>A scala object</li>
     * </ul>
     *
     * @param mod   the mod which the object is from
     * @param value the string declaration of the object
     * @param type  the type that the created object must be an instance of
     * @param <T>   the type, such as {@link net.fabricmc.api.ModInitializer}
     * @return the mod instance
     * @throws LanguageAdapterException if an error occurred in mod constructing
     */
    @Override
    public <T> T create(ModContainer mod, String value, Class<T> type) throws LanguageAdapterException {
        // LOGGER.debug("Loading {} of  Scala mod({}) from {}", type, mod, value);
        if (value.endsWith("$")) {
            // Scala object
            // LOGGER.debug("({}) Detect object {}", mod, value);
            try {
                Object obj = getScalaObject(mod, value);
                // LOGGER.debug("({}) Got object instance {}, interface={}", mod, obj, obj.getClass().getInterfaces());
                return type.cast(obj);
            } catch (ReflectiveOperationException | ClassCastException e) {
                throw new LanguageAdapterException(String.format("Error in getting scala object of %s from %s", type, value), e);
            }
        } else {
            // LOGGER.debug("({}) Detect class {}", mod, value);
            // Check object
            try {
                Class<?> clazz = Class.forName(value + "$", false, ScalaLanguageAdapter.class.getClassLoader());
                // LOGGER.debug("({}) Got object class {}", mod, clazz);
                Object obj = getScalaObject(mod, value + "$");
                // LOGGER.debug("({}) Got object instance {}, interface={}", mod, obj, obj.getClass().getInterfaces());
                if (type.isInstance(obj)) {
                    // LOGGER.debug("({}) The object is implementing {}, return", mod, type);
                    return type.cast(obj);
                }
            } catch (ReflectiveOperationException | ClassCastException ignore) {
                // LOGGER.debug("({}) {} doesn't have object for {}", mod, value, type);
                // Object doesn't exist or object is not the class instance.
            }

            try {
                Class<?> clazz = Class.forName(value, true, ScalaLanguageAdapter.class.getClassLoader());
                // LOGGER.debug("({}) Call constructor of {}", mod, value);
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                Object clsObject = constructor.newInstance();
                // LOGGER.debug("({}) Got class instance {}, interface={}", mod, clsObject, clsObject.getClass().getInterfaces());
                return type.cast(clsObject);
            } catch (ReflectiveOperationException | ClassCastException e) {
                throw new LanguageAdapterException(String.format("Error in constructing an instance of %s from %s", type, value), e);
            }
        }
    }

    private static Object getScalaObject(ModContainer mod, String value) throws ReflectiveOperationException, ClassCastException {
        // LOGGER.debug("({}) Try to get MODULE$ from {}", mod, value);
        Class<?> clazz = Class.forName(value, true, ScalaLanguageAdapter.class.getClassLoader());
        return clazz.getField("MODULE$").get(null);
    }
}
