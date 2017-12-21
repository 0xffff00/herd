package tmp.learning;

import java.nio.file.Path;
        import java.nio.file.Paths;

public class LearnPathSlash {
    public static void main(String[] args) {
        // the path seperator for this system
        String pathSep = System.getProperty("path.separator");

        // my home directory
        Path homeDir = Paths.get(System.getProperty("user.home"));

        // lets print them
        System.out.println("Path Sep: " + pathSep);
        System.out.println(homeDir.toAbsolutePath());

        // as it turns out, on my linux it is a colon
        // and Java is using forward slash internally
        // lets add some more directories to the user.home

        homeDir = homeDir.resolve("eclipse").resolve("configuration");
        System.out.println("Appending more directories using resolve()");
        System.out.println(homeDir);

    }
}
