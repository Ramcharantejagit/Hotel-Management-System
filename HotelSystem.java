import java.io.*;
import java.util.*;

class Food implements Serializable {
    private static final String[] ITEMS = {"Sandwich", "Pasta", "Noodles", "Coke"};
    private static final float[] PRICES = {50, 60, 70, 30};

    int itemId;
    int quantity;
    float price;

    public Food(int itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.price = quantity * PRICES[itemId - 1];
    }

    @Override
    public String toString() {
        return String.format("%-10s %-10d Rs.%.2f", ITEMS[itemId - 1], quantity, price);
    }
}

abstract class Room implements Serializable {
    String guest1Name;
    String guest1Contact;
    String guest1Gender;
    List<Food> orders = new ArrayList<>();

    public Room(String name, String contact, String gender) {
        this.guest1Name = name;
        this.guest1Contact = contact;
        this.guest1Gender = gender;
    }

    public abstract double getRoomCharge();

    public void addFood(Food food) {
        orders.add(food);
    }

    public void printBill() {
        double total = getRoomCharge();
        System.out.println("Room Charge: Rs." + getRoomCharge());
        if (!orders.isEmpty()) {
            System.out.println("\nFood Charges:");
            System.out.println("Item       Quantity   Price");
            System.out.println("-----------------------------");
            for (Food f : orders) {
                System.out.println(f);
                total += f.price;
            }
        }
        System.out.printf("\nTotal Amount: Rs.%.2f\n", total);
    }
}

class SingleRoom extends Room {
    public SingleRoom(String name, String contact, String gender) {
        super(name, contact, gender);
    }

    @Override
    public double getRoomCharge() {
        return 2200;
    }
}

class DoubleRoom extends Room {
    String guest2Name;
    String guest2Contact;
    String guest2Gender;

    public DoubleRoom(String name1, String contact1, String gender1,
                      String name2, String contact2, String gender2) {
        super(name1, contact1, gender1);
        this.guest2Name = name2;
        this.guest2Contact = contact2;
        this.guest2Gender = gender2;
    }

    @Override
    public double getRoomCharge() {
        return 4000;
    }
}

class Hotel implements Serializable {
    List<Room> luxuryDoubleRooms = new ArrayList<>(Collections.nCopies(10, null));
    List<Room> deluxeDoubleRooms = new ArrayList<>(Collections.nCopies(20, null));
    List<Room> luxurySingleRooms = new ArrayList<>(Collections.nCopies(10, null));
    List<Room> deluxeSingleRooms = new ArrayList<>(Collections.nCopies(20, null));
}

class HotelManager {
    private static final Scanner scanner = new Scanner(System.in);
    private static Hotel hotel;
    private static final String BACKUP_FILE = "hotel_backup.ser";

    public static void main(String[] args) {
        loadHotelData();

        while (true) {
            System.out.println("\n1. View Room Features");
            System.out.println("2. Check Availability");
            System.out.println("3. Book a Room");
            System.out.println("4. Order Food");
            System.out.println("5. Checkout");
            System.out.println("6. Exit");

            int choice = getIntInput("Choose an option: ");
            switch (choice) {
                case 1 -> displayRoomFeatures();
                case 2 -> checkAvailability();
                case 3 -> bookRoom();
                case 4 -> orderFood();
                case 5 -> checkout();
                case 6 -> {
                    saveHotelData();
                    return;
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    private static void displayRoomFeatures() {
        System.out.println("\nRoom Types:");
        System.out.println("1. Luxury Double Room - AC, Free Breakfast, Rs.4000/day");
        System.out.println("2. Deluxe Double Room - Non-AC, Free Breakfast, Rs.3000/day");
        System.out.println("3. Luxury Single Room - AC, Free Breakfast, Rs.2200/day");
        System.out.println("4. Deluxe Single Room - Non-AC, Free Breakfast, Rs.1200/day");
    }

    private static void checkAvailability() {
        System.out.println("\nAvailability:");
        System.out.printf("Luxury Double Rooms: %d available\n", availableRooms(hotel.luxuryDoubleRooms));
        System.out.printf("Deluxe Double Rooms: %d available\n", availableRooms(hotel.deluxeDoubleRooms));
        System.out.printf("Luxury Single Rooms: %d available\n", availableRooms(hotel.luxurySingleRooms));
        System.out.printf("Deluxe Single Rooms: %d available\n", availableRooms(hotel.deluxeSingleRooms));
    }

    private static void bookRoom() {
        System.out.println("\nRoom Types:");
        System.out.println("1. Luxury Double Room");
        System.out.println("2. Deluxe Double Room");
        System.out.println("3. Luxury Single Room");
        System.out.println("4. Deluxe Single Room");
        int type = getIntInput("Enter room type: ");
        List<Room> roomList = getRoomList(type);
        if (roomList == null) {
            System.out.println("Invalid room type.");
            return;
        }

        for (int i = 0; i < roomList.size(); i++) {
            if (roomList.get(i) == null) System.out.print((i + 1) + " ");
        }

        int roomNum = getIntInput("\nEnter room number to book: ") - 1;
        if (roomNum < 0 || roomNum >= roomList.size() || roomList.get(roomNum) != null) {
            System.out.println("Room not available.");
            return;
        }

        System.out.print("Enter name: ");
        String name = scanner.next();
        System.out.print("Enter contact: ");
        String contact = scanner.next();
        System.out.print("Enter gender: ");
        String gender = scanner.next();

        if (type == 1 || type == 2) {
            System.out.print("Enter 2nd guest name: ");
            String name2 = scanner.next();
            System.out.print("Enter contact: ");
            String contact2 = scanner.next();
            System.out.print("Enter gender: ");
            String gender2 = scanner.next();
            roomList.set(roomNum, new DoubleRoom(name, contact, gender, name2, contact2, gender2));
        } else {
            roomList.set(roomNum, new SingleRoom(name, contact, gender));
        }

        System.out.println("Room booked successfully.");
    }

    private static void orderFood() {
        int roomNum = getIntInput("Enter room number: ") - 1;
        int roomType = getIntInput("Enter room type (1-4): ");
        List<Room> roomList = getRoomList(roomType);

        if (roomList == null || roomNum < 0 || roomNum >= roomList.size() || roomList.get(roomNum) == null) {
            System.out.println("Room not found.");
            return;
        }

        Room room = roomList.get(roomNum);

        while (true) {
            System.out.println("\nMenu:\n1. Sandwich - Rs.50\n2. Pasta - Rs.60\n3. Noodles - Rs.70\n4. Coke - Rs.30");
            int itemId = getIntInput("Select item: ");
            int qty = getIntInput("Enter quantity: ");
            room.addFood(new Food(itemId, qty));

            System.out.print("Order more? (y/n): ");
            if (!scanner.next().equalsIgnoreCase("y")) break;
        }
    }

    private static void checkout() {
        int roomNum = getIntInput("Enter room number: ") - 1;
        int roomType = getIntInput("Enter room type (1-4): ");
        List<Room> roomList = getRoomList(roomType);

        if (roomList == null || roomNum < 0 || roomNum >= roomList.size() || roomList.get(roomNum) == null) {
            System.out.println("Room not found.");
            return;
        }

        roomList.get(roomNum).printBill();
        roomList.set(roomNum, null);
        System.out.println("Checkout completed.");
    }

    private static int availableRooms(List<Room> rooms) {
        return (int) rooms.stream().filter(Objects::isNull).count();
    }

    private static List<Room> getRoomList(int type) {
        return switch (type) {
            case 1 -> hotel.luxuryDoubleRooms;
            case 2 -> hotel.deluxeDoubleRooms;
            case 3 -> hotel.luxurySingleRooms;
            case 4 -> hotel.deluxeSingleRooms;
            default -> null;
        };
    }

    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid. " + prompt);
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static void loadHotelData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BACKUP_FILE))) {
            hotel = (Hotel) ois.readObject();
        } catch (Exception e) {
            hotel = new Hotel(); // start fresh if backup missing/corrupt
        }
    }

    private static void saveHotelData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BACKUP_FILE))) {
            oos.writeObject(hotel);
            System.out.println("Hotel data saved.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }
}
