package api.dao.comparison;

import java.lang.reflect.Field;
import java.util.Map;

public class DaoComparator {

    private final DaoComparisonConfigLoader configLoader;

    public DaoComparator() {
        this.configLoader = new DaoComparisonConfigLoader("dao-comparison.properties");
    }

    public void compare(Object apiResponse, Object dao) {
        DaoComparisonConfigLoader.DaoComparisonRule rule = configLoader.getRuleFor(apiResponse.getClass());

        if (rule == null) {
            throw new RuntimeException("No comparison rule found for " + apiResponse.getClass().getSimpleName());
        }

        Map<String, String> fieldMappings = rule.getFieldMappings();

        for (Map.Entry<String, String> mapping : fieldMappings.entrySet()) {
            String apiFieldName = mapping.getKey();
            String daoFieldName = mapping.getValue();

            Object apiValue = getFieldValue(apiResponse, apiFieldName);
            Object daoValue = getFieldValue(dao, daoFieldName);

            if (!areValuesEqual(apiValue, daoValue)) {
                throw new AssertionError(String.format(
                        "Field mismatch for %s: API=%s, DAO=%s",
                        apiFieldName, apiValue, daoValue));
            }
        }
    }

    private static boolean areValuesEqual(Object expected, Object actual) {
        if (expected == null && actual == null) return true;
        if (expected == null || actual == null) return false;

        String expectedStr = String.valueOf(expected);
        String actualStr = String.valueOf(actual);

        if (isTimestamp(expectedStr) && isTimestamp(actualStr)) {
            expectedStr = normalizeTimestamp(truncateToMillis(expectedStr));
            actualStr = normalizeTimestamp(truncateToMillis(actualStr));
            return expectedStr.equals(actualStr);
        }

        return java.util.Objects.equals(expectedStr, actualStr);
    }

    private static boolean isTimestamp(String str) {
        return str != null && str.matches("^\\d{4}-\\d{2}-\\d{2}[T ]\\d{2}:\\d{2}:\\d{2}\\.\\d+$");
    }

    private static String normalizeTimestamp(String timestamp) {
        return timestamp.replace(" ", "T");
    }

    private static String truncateToMillis(String timestamp) {
        return timestamp.replaceAll("(\\.\\d{3})\\d*", "$1");
    }

    private Object getFieldValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to get field value: " + fieldName, e);
        }
    }

    private static class Objects {
        public static boolean equals(Object a, Object b) {
            return (a == b) || (a != null && a.equals(b));
        }
    }
}
