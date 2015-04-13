public class Project3Main {

    public static void main(String[] args) {
        if (args.length < 4) {
            exitWithErrorMessage("Usage: java Proj3");
        }
    }

    private static void exitWithErrorMessage(String errorMessage) {
        System.out.format(errorMessage);
        System.exit(1);
    }

}
