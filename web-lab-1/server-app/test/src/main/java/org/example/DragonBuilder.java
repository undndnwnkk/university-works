package org.example;

import java.time.ZonedDateTime;
import java.util.Scanner;

public class DragonBuilder {
    public static Object[] build() {
        Scanner scanner = new Scanner(System.in);
        Object[] result = new Object[8];

        System.out.println("Введите имя дракона: ");
        result[0] = scanner.nextLine().trim();

        Coordinates coordinates = new Coordinates();
        Long x;
        while (true) {
            System.out.print("Введите координату X(> -384)");
            try {
                x = Long.parseLong(scanner.nextLine().trim());
                if(x > -384) break;
                else System.out.println("Неверное значение, попробуйте еще раз");
            } catch (NumberFormatException e) {
                System.out.println("Неверное значение, попробуйте еще раз");
            }
        }

        double y;
        while (true) {
            System.out.print("Введите координату Y(< 271) ");
            try {
                y = Double.parseDouble(scanner.nextLine().trim());
                if (y < 271) break;
                else System.out.println("Введите число меньше");
            } catch (NumberFormatException e) {
                System.out.println("Неверное значение, попробуйте еще раз");
            }
        }
        coordinates.setX(x);
        coordinates.setY(y);
        result[1] = coordinates;

        result[2] = ZonedDateTime.now();

        int age;
        System.out.println("Введите возраст дракона: ");
        while (true) {
            try {
                age =  Integer.parseInt(scanner.nextLine().trim());
                if(age <= 0) System.out.println("Возраст должен быть > 0");
                else break;
            } catch (NumberFormatException e) {
                System.out.println("Неверный тип данных для возраста. Попробуйте еще раз");
            }
        }
        result[3] = age;

        System.out.println("Введите длину крыла: ");
        float wingspan;
        while (true) {
            try {
                wingspan = Float.parseFloat(scanner.nextLine().trim());
                if(wingspan < 0) System.out.println("Введите число > 0");
                else break;
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат. Попробуйте еще раз");
            }
        }

        result[4] = wingspan;

        DragonType dragonType = new DragonType();
        System.out.println("Введите тип дракона( WATER, UNDERGROUND, AIR, FIRE): ");
        while (true) {
            String dragonTypeName = scanner.nextLine().trim().toUpperCase();
            try {
                dragonType = DragonType.valueOf(dragonTypeName);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Напишите тип дракона из списка выше");
            }
        }
        result[5] = dragonType;

        DragonCharacter dragonCharacter = new DragonCharacter();
        System.out.println("Введите характер дракона(CUNNING, WISE, GOOD, CHAOTIC): ");
        while (true) {
            String dragonCharacterName = scanner.nextLine().trim().toUpperCase();
            try {
                dragonCharacter = DragonCharacter.valueOf(dragonCharacterName);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Напишите характер дракона из списка выше");
            }
        }

        result[6] = dragonCharacter;

        System.out.println("Хотите добавить убийцу? (yes/no):");
        String hasKiller = scanner.nextLine().trim().toLowerCase();

        if (hasKiller.equals("yes")) {
            System.out.println("Введите имя убийцы:");
            String killerName = scanner.nextLine().trim();
            while (killerName.isEmpty()) {
                System.out.println("Имя не может быть пустым. Попробуйте снова:");
                killerName = scanner.nextLine().trim();
            }

            System.out.println("Введите дату рождения убийцы (формат ISO, например: 2011-12-03T10:15:30+03:00[Europe/Moscow]):");
            ZonedDateTime birthday = null;
            while (birthday == null) {
                try {
                    String dateStr = scanner.nextLine().trim();
                    if (dateStr.isEmpty()) {
                        System.out.println("Дата не может быть пустой. Попробуйте снова:");
                        continue;
                    }
                    birthday = ZonedDateTime.parse(dateStr);
                } catch (Exception e) {
                    System.out.println("Некорректный формат даты. Попробуйте снова:");
                }
            }

            System.out.println("Введите рост убийцы (должен быть > 0):");
            Double height = null;
            while (height == null) {
                try {
                    String heightStr = scanner.nextLine().trim();
                    if (heightStr.isEmpty()) {
                        System.out.println("Рост не может быть пустым. Попробуйте снова:");
                        continue;
                    }
                    height = Double.parseDouble(heightStr);
                    if (height <= 0) {
                        System.out.println("Рост должен быть > 0. Попробуйте снова:");
                        height = null;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Некорректный формат числа. Попробуйте снова:");
                }
            }

            System.out.println("Выберите национальность убийцы (доступные национальности: " + Arrays.toString(Country.values()) + ") или нажмите Enter для пропуска:");
            Country nationality = null;
            String nationalityStr = scanner.nextLine().trim().toUpperCase();
            if (!nationalityStr.isEmpty()) {
                try {
                    nationality = Country.valueOf(nationalityStr);
                } catch (IllegalArgumentException e) {
                    System.out.println("Некорректная национальность. Пропускаем.");
                }
            }

            System.out.println("Хотите добавить местоположение убийцы? (yes/no):");
            String hasLocation = scanner.nextLine().trim().toLowerCase();
            Location killerLocation = null;
            if (hasLocation.equals("yes")) {
                System.out.println("Введите координату X местоположения:");
                Integer locX = null;
                while (locX == null) {
                    try {
                        String xStr = scanner.nextLine().trim();
                        if (xStr.isEmpty()) {
                            System.out.println("Координата X не может быть пустой. Попробуйте снова:");
                            continue;
                        }
                        locX = Integer.parseInt(xStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Некорректный формат числа. Попробуйте снова:");
                    }
                }

                System.out.println("Введите координату Y местоположения:");
                Integer locY = null;
                while (locY == null) {
                    try {
                        String yStr = scanner.nextLine().trim();
                        if (yStr.isEmpty()) {
                            System.out.println("Координата Y не может быть пустой. Попробуйте снова:");
                            continue;
                        }
                        locY = Integer.parseInt(yStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Некорректный формат числа. Попробуйте снова:");
                    }
                }

                System.out.println("Введите название местоположения (или нажмите Enter для пропуска):");
                String locName = scanner.nextLine().trim();
                killerLocation = new Location(locX, locY, locName.isEmpty() ? null : locName);
            }

            Person killer = new Person(killerName, birthday, height, nationality, killerLocation);
            result[7] = killer;
        }
        return result;
    }


}
