package net.kalinovcic.kinetix.math;

public class Distribution
{
    public static class Packing
    {
        public double width;
        public double height;
        public double startX;
        public double startY;
        public int countX;
        public int countY;
    }
    
    public static Packing findOptimalPacking(double w, double h, int n)
    {
        double low = 0;
        double high = Math.max(w, h); 
        while (low + 0.01 < high)
        {
            double mid = low + (high - low) / 2;

            double ew = w - 2 * mid;
            double eh = h - 2 * mid;

            double hw = mid;
            double hh = Math.sqrt(mid * mid * 0.75);

            int cw = (int) Math.floor(ew / hw) + 1;
            int ch = (int) Math.floor(eh / hh) + 1;
            
            int c = (ch / 2 + ch % 2) * cw + (ch / 2) * (cw - 1);
            if (c >= n) low = mid;
            else high = mid;
        }
        
        Packing packing = new Packing();
        packing.width = low;
        packing.height = Math.sqrt(low * low * 0.75);
        packing.countX = (int) Math.floor((w - 2 * low) / packing.width) + 1;
        packing.countY = (int) Math.floor((h - 2 * low) / packing.height) + 1;
        packing.startX = (w - (packing.countX - 1) * packing.width) / 2;
        packing.startY = (h - (packing.countY - 1) * packing.height) / 2;
        return packing;
    }
}
