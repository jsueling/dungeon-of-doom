import java.io.*;
import java.util.*;

public class MapLoader {

    // stores paths of map files
    private final ArrayList<String> mapFilePaths = new ArrayList<>();

    // loads maps (.txt files which are not README.txt) from the current directory
    // and populates mapFilePaths when instantiated
    public MapLoader() {
        this.getMapFilePaths();
    }

    // populates mapFilePaths with absolute paths of all valid map files
    private void getMapFilePaths() {
        // get maps directory
        File mapsDirectory = new File("maps");
        // get all files in maps directory
        File[] mapFiles = mapsDirectory.listFiles();
        // credits to: https://stackoverflow.com/a/37152114
        // IntelliJ warning: if abstract pathname does not denote a directory
        // then listFiles() returns null. The solution is to check for not null
        if (mapFiles != null) {
            for (File file : mapFiles) {
                if (isValidMapFile(file)) {
                    this.mapFilePaths.add(file.getPath());
                }
            }
        }
    }

    // is the file passed as a parameter a .txt file that is not README.txt
    private boolean isValidMapFile(File file) {
        String fileName = file.getName();
        return fileName.endsWith(".txt") && !fileName.startsWith("README");
    }

    // prints all valid maps, numbered (one-indexed) for the user to select
    public void printMapSelection() {

        // number of valid maps found
        int len = this.mapFilePaths.size();

        // none found, terminate program by throwing a runtime exception
        if (len == 0) {
            throw new RuntimeException("No valid map files found in the current directory (.txt only excluding README.txt)");
        }

        // iterate over each map
        for (int index = 0; index < len; index++) {

            String pathName = this.mapFilePaths.get(index);
            // pathName is absolute path: "./maps/example_map.txt"

            int startMapName = pathName.lastIndexOf('/') + 1;
            int endMapName = pathName.lastIndexOf('.');

            String mapName = pathName.substring(startMapName, endMapName);
            // mapName after slicing: "example_map"

            // print index + mapName (one-indexed)
            System.out.printf("%d. %s\n", index + 1, mapName);
        }
    }

    // prompts the user to select a map, instantiates and returns the map based on selection
    public Map chooseMap(UserInput userInput) {

        System.out.print("Select a map by entering the index: ");

        // delegate all responsibility to UserInput regarding handling user input
        // getIndexWithinRange will repeatedly prompt the user for a valid index
        // according to the list size parameter passed in
        int listIndex = userInput.getIndexWithinRange(this.mapFilePaths.size());

        return new Map(this.mapFilePaths.get(listIndex));
    }
}
