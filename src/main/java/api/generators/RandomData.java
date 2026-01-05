package api.generators;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomData {
    private RandomData() {
    }

    public static String getUsername() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static String getPassword() {
        return RandomStringUtils.randomAlphabetic(3).toUpperCase() +
                RandomStringUtils.randomAlphabetic(5).toLowerCase() +
                RandomStringUtils.randomNumeric(3) + "$%";
    }

    public static double getValidDepositAmount() {
        double value = ThreadLocalRandom.current().nextDouble(0.02, 5000);
        return Math.round(value * 100.0) / 100.0;
    }

    public static double getRandomNegativeAmount() {
        double value = ThreadLocalRandom.current().nextDouble(-1_000_000.00, -0.01);
        return Math.round(value * 100.0) / 100.0;
    }

    public static double getRandomInvalidDepositPositiveAmount() {
        double value = ThreadLocalRandom.current().nextDouble(5000.02, 1_000_000.00);
        return Math.round(value * 100.0) / 100.0;
    }

    public static double getRandomInvalidTransferPositiveAmount() {
        double value = ThreadLocalRandom.current().nextDouble(10000.02, 1_000_000.00);
        return Math.round(value * 100.0) / 100.0;
    }

    public static double getValidTransferAmount() {
        double value = ThreadLocalRandom.current().nextDouble(0.02, 10000);
        return Math.round(value * 100.0) / 100.0;
    }

    public static String getValidUserName() {
        int length1 = (int) (Math.random() * 20) + 1;
        int length2 = (int) (Math.random() * 20) + 1;

        String name = RandomStringUtils.randomAlphabetic(length1);
        String surname = RandomStringUtils.randomAlphabetic(length2);

        return name + " " + surname;
    }

    public static long getNonExistingAccountId() {
        return ThreadLocalRandom.current().nextLong(30_000, 100_000);
    }

    public static String getInvalidAccountNumber() {
        int number = ThreadLocalRandom.current().nextInt(10000, 30001);
        return "ACC" + number;
    }
}
