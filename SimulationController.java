
import java.io.*;

public class SimulationController {

    private final PrManager pr;
    private final OtherKerServices os;
    private String cachedLine = null;
    private double curTime = 0.0;

    public SimulationController(PrManager pr, OtherKerServices os) {
        this.pr = pr;
        this.os = os;
    }

    public void run(File input, String mode) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(input))) {
            cachedLine = readNextNonEmpty(br);

            while (cachedLine != null || pr.hasRunnableWork()) {
                double i = parseNextExternalTime(cachedLine);
                double e = pr.getNextInternalEventTime(curTime);
                double next = Math.min(i, e);

                if (Double.isInfinite(next)) {
                    break;
                }

                double tiny = 0.000000001;
                if (!Double.isInfinite(e) && Math.abs(e - next) < tiny) {
                    curTime = e;
                    pr.cpuTimeAdvance(curTime);
                    pr.handleInternalEvent();
                } else if (e < i) {
                    curTime = e;
                    pr.cpuTimeAdvance(curTime);
                    pr.handleInternalEvent();
                } else {
                    curTime = i;
                    pr.cpuTimeAdvance(curTime);
                    handleExternal(cachedLine, mode);
                    cachedLine = readNextNonEmpty(br);
                }
            }
        }

        if (pr.isFinalDisplay()) {
            System.out.printf(java.util.Locale.US,
                    "%n%n--- Simulation finished at time %.1f ---%n%n",
                    curTime);
        }
    }

    private static String readNextNonEmpty(BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty()) {
                return line;
            }
        }
        return null;
    }

    private static double parseNextExternalTime(String line) {
        if (line == null) {
            return Double.POSITIVE_INFINITY;
        }
        try {
            String[] token = line.split("\\s+");
            if (token.length < 2) {
                return Double.POSITIVE_INFINITY;
            }
            return Double.parseDouble(token[1]);
        } catch (Exception ex) {
            return Double.POSITIVE_INFINITY;
        }
    }

    private void handleExternal(String line, String mode) {
        if (line == null) {
            return;
        }

        String[] tok = line.trim().split("\\s+");
        if (tok.length == 0) {
            return;
        }

        char type = tok[0].charAt(0);

        switch (type) {
            case 'C': {
                pr.clearAll(); // We'll define this method
                int M = parseKey(line, 'M');
                int S = parseKey(line, 'S');
                double t = Double.parseDouble(tok[1]);
                curTime = t;
                pr.cpuTimeAdvance(curTime);
                os.configure(M, S);

                Scheduler sched;
                String schedName;

                if ("static".equals(mode)) {
                    sched = new SRoundRobinScheduler(13);
                    schedName = "StaticRR";
                } else {
                    sched = new DRoundRobinScheduler();
                    schedName = "DynamicRR";
                }

                pr.setScheduler(sched);

                System.out.printf(java.util.Locale.US,
                        "CONFIG at %.2f: mem=%d devices=%d scheduler=%s%n",
                        curTime, M, S, schedName);
                System.out.println();

                break;
            }

            case 'A': {
                int J = parseKey(line, 'J');
                int M = parseKey(line, 'M');
                int S = parseKey(line, 'S');
                int R = parseKey(line, 'R');
                int P = parseKey(line, 'P');
                pr.procArrivalRoutine(new Process(J, curTime, M, S, R, P));
                break;
            }

            case 'D':
                double displayTime = Double.parseDouble(tok[1]);
                boolean isFinal = (displayTime == 999999.0);
                pr.display(curTime, os, isFinal);
                 if (displayTime == 999999.0) {
                    pr.markFinalDisplay(); // mark only for 999999
                }
                break;

            default:
                break;
        }
    }

    private static int parseKey(String line, char k) {
        int i = line.indexOf(k + "=");
        if (i < 0) {
            return 0;
        }

        int j = i + 2;
        StringBuilder sb = new StringBuilder();

        while (j < line.length()
                && (Character.isDigit(line.charAt(j)) || line.charAt(j) == '.')) {
            sb.append(line.charAt(j++));
        }

        try {
            return (int) Math.round(Double.parseDouble(sb.toString()));
        } catch (Exception e) {
            return 0;
        }
    }
}
