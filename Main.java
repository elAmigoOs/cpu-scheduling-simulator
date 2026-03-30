
import java.io.File;
import java.io.PrintStream;

public class Main {

    public static void main(String[] args) throws Exception {

        String inputFileName = "inputSRR.txt";
        String mode;

        if (inputFileName.contains("SRR")) {
            mode = "static";
        } else if (inputFileName.contains("DRR")) {
            mode = "dynamic";
        } else {
            throw new IllegalArgumentException("Filename must contain 'SRR' or 'DRR'");
        }

        PrintStream out = new PrintStream("output.txt");
        System.setOut(out);

        File inputFile = new File(inputFileName);

        OtherKerServices osManager = new OtherKerServices();
        PrManager processManager = new PrManager(osManager);
        SimulationController controller = new SimulationController(processManager, osManager);

        controller.run(inputFile, mode);

        out.close();
    }
}
