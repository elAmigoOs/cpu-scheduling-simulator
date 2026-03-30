
public class OtherKerServices {

    int totalMem = 0;
    int totalDev = 0;
    int availMem = 0;
    int availDev = 0;

    void configure(int mem, int dev) {
        totalMem = mem;
        totalDev = dev;
        availMem = mem;
        availDev = dev;
    }

    boolean canAllocate(int mem, int dev) {
        return mem <= availMem && dev <= availDev;
    }

    void allocate(int mem, int dev) {
        availMem -= mem;
        availDev -= dev;
    }

    void release(int mem, int dev) {
        availMem += mem;
        availDev += dev;
    }
}
