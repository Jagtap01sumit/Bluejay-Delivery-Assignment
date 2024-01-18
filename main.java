import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class Employee {
    String name;
    Date startTime;
    Date endTime;

    public Employee(String name, Date startTime, Date endTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}

public class main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the path of the CSV file: "); // take the img csv file
        String filePath = scanner.nextLine();

        List<Employee> employees = readEmployeeDataFromFile(filePath);

        if (employees != null && !employees.isEmpty()) {
            analyzeConsecutiveDays(employees); // function 1
            analyzeTimeBetweenShifts(employees); // function 2
            analyzeLongSingleShifts(employees); // function 3
        } else {
            System.out.println("No valid data found in the CSV file.");
        }

        scanner.close(); // exit the code
    }

    private static List<Employee> readEmployeeDataFromFile(String filePath) {
        List<Employee> employees = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split the data using comma
                String[] data = line.split(",");

                // Check if the data array has at least 4 elements (name, start time, end time,
                // duration)
                if (data.length >= 4) {
                    // Extract info
                    String name = data[0].replaceAll("\"", "").trim(); // Remove quotes and trim spaces
                    String startTimeStr = data[2].trim(); // Assuming start time is at index 2
                    String endTimeStr = data[3].trim(); // Assuming end time is at index 3

                    Date startTime = parseDate(startTimeStr);
                    Date endTime = parseDate(endTimeStr);

                    if (startTime != null && endTime != null) {
                        employees.add(new Employee(name, startTime, endTime));
                    }
                } else {
                    System.out.println("Invalid data format: " + Arrays.toString(data));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return employees;
    }

    private static Date parseDate(String dateString) {
        if ("Time".equalsIgnoreCase(dateString)) {
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            System.out.println("Error parsing date: " + dateString);
            return null;
        }
    }

    private static void analyzeConsecutiveDays(List<Employee> employees) {

        for (Employee employee : employees) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(employee.startTime);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(employee.endTime);

            long differenceInDays = (cal2.getTimeInMillis() - cal1.getTimeInMillis()) / (24 * 60 * 60 * 1000);

            if (differenceInDays >= 7) {
                System.out.println(employee.name + " has worked for 7 consecutive days.");
            }
        }
    }

    private static void analyzeTimeBetweenShifts(List<Employee> employees) {
        for (Employee employee : employees) {
            long timeBetweenShifts = employee.endTime.getTime() - employee.startTime.getTime();

            // Example
            if (timeBetweenShifts > 3600000 && timeBetweenShifts < 10 * 3600000) {
                System.out.println(employee.name + " has less than 10 hours between shifts but more than 1 hour.");
            }
        }
    }

    private static void analyzeLongSingleShifts(List<Employee> employees) {
        for (Employee employee : employees) {
            long shiftDuration = employee.endTime.getTime() - employee.startTime.getTime();
            // Example
            if (shiftDuration > 50400000) {
                System.out.println(employee.name + " has worked for more than 14 hours in a single shift.");
            }
        }
    }
}
