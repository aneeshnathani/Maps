
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    public static final double ROOT_ULLAT = 37.892195547244356, ROOT_ULLON = -122.2998046875,
            ROOT_LRLAT = 37.82280243352756, ROOT_LRLON = -122.2119140625;
    private double requiredResolution;
    private int depth;
    private double UserULLAT;
    private double UserULLON;
    private double UserLRLAT;
    private double UserLRLON;
    private double Raster_ULLON;
    private double Raster_ULLAT;
    private double Raster_LRLON;
    private double Raster_LRLAT;
    private boolean querySuccess;

    public Rasterer() {
        querySuccess = true;
    }

    public int findRequiredDepth(double rr) {
        double maxRes = 98.94561767578125;
        int dpth = 0;
        while (maxRes > rr) {
            if (dpth == 7) {
                break;
            }
            maxRes = maxRes / 2.0;
            dpth++;
        }
        return dpth;

    }


    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     * The grid of images must obey the following properties, where image in the
     * grid is referred to as a "tile".
     * <ul>
     * <li>The tiles collected must cover the most longitudinal distance per pixel
     * (LonDPP) possible, while still covering less than or equal to the amount of
     * longitudinal distance per pixel in the query box for the user viewport size. </li>
     * <li>Contains all tiles that intersect the query bounding box that fulfill the
     * above condition.</li>
     * <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     * </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     * @return A map of results for the front end as specified: <br>
     * "renderGriddy"   : String[][], the files to display. <br>
     * "Raster_ULLON" : Number, the bounding upper left longitude of the rastered image. <br>
     * "Raster_ULLAT" : Number, the bounding upper left latitude of the rastered image. <br>
     * "Raster_LRLON" : Number, the bounding lower right longitude of the rastered image. <br>
     * "Raster_LRLAT" : Number, the bounding lower right latitude of the rastered image. <br>
     * <<<<<<< HEAD
     * "depth"         : Number, the depth of the nodes of the rastered image;
     * can also be interpreted as the length of the numbers in the image
     * string. <br>
     * =======
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * >>>>>>> 0ff9935f3e4eef382e47c567733a55bb2bd2bcab
     * "querySuccess" : Boolean, whether the query was able to successfully complete; don't
     * forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        Map<String, Object> results = new HashMap<>();
        double longPixel = (params.get("lrlon") - params.get("ullon")) / params.get("w");
        requiredResolution = longPixel * 288200;
        depth = findRequiredDepth(requiredResolution);
        UserLRLAT = params.get("lrlat");
        UserLRLON = params.get("lrlon");
        UserULLAT = params.get("ullat");
        UserULLON = params.get("ullon");
        int firstx = (int) Math.floor((UserULLON - ROOT_ULLON)
                /
                ((ROOT_LRLON - ROOT_ULLON) / (Math.pow(2, depth))));
        int lastx = (int) Math.pow(2, depth) - 1
                - (int) Math.floor((ROOT_LRLON - UserLRLON)
                / ((ROOT_LRLON - ROOT_ULLON) / (Math.pow(2, depth))));
        int yfirst = (int) Math.floor((ROOT_ULLAT - UserULLAT)
                / ((ROOT_ULLAT - ROOT_LRLAT) / (Math.pow(2, depth))));
        int ylast = (int) Math.pow(2, depth) - 1
                - (int) Math.floor((UserLRLAT - ROOT_LRLAT)
                / ((ROOT_ULLAT - ROOT_LRLAT) / (Math.pow(2, depth))));
        Image[][] renderGrid = new Image[ylast - yfirst + 1][lastx - firstx + 1];
        String[][] renderGriddy = new String[ylast - yfirst + 1][lastx - firstx + 1];
        for (int kk = firstx; kk <= lastx; kk++) {
            for (int jj = yfirst; jj <= ylast; jj++) {
                renderGrid[jj - yfirst][kk - firstx] = new Image(kk, jj, depth);
                renderGriddy[jj - yfirst][kk - firstx] = new Image(kk, jj, depth).name;


            }
        }
        Raster_ULLON = renderGrid[0][0].upperLeftLongitude;
        Raster_ULLAT = renderGrid[0][0].upperLeftLatitude;
        Raster_LRLAT = renderGrid[ylast - yfirst][lastx - firstx].lowerRightLatitude;
        Raster_LRLON = renderGrid[ylast - yfirst][lastx - firstx].lowerRightLongitude;

        results.put("render_grid", renderGriddy);
        results.put("raster_ul_lon", Raster_ULLON);
        results.put("raster_ul_lat", Raster_ULLAT);
        results.put("raster_lr_lon", Raster_LRLON);
        results.put("raster_lr_lat", Raster_LRLAT);
        results.put("depth", depth);
        results.put("query_success", querySuccess);
        return results;
    }

    public class Image {
        String name;
        double upperLeftLongitude;
        double upperLeftLatitude;
        double lowerRightLongitude;
        double lowerRightLatitude;
        int depthofImage;
        int xx;
        int yy;

        Image(int x, int y, int depth) {
            name = "d" + depth + "_" + "x" + x + "_" + "y" + y + ".png";
            xx = x;
            yy = y;
            depthofImage = depth;
            upperLeftLongitude = ((ROOT_LRLON - ROOT_ULLON)
                    / (Math.pow(2, depthofImage))) * x + ROOT_ULLON;
            lowerRightLongitude = upperLeftLongitude + (ROOT_LRLON - ROOT_ULLON)
                    / (Math.pow(2, depthofImage));
            upperLeftLatitude = ROOT_ULLAT - ((ROOT_ULLAT - ROOT_LRLAT)
                    / (Math.pow(2, depthofImage))) * y;
            lowerRightLatitude = upperLeftLatitude - (ROOT_ULLAT - ROOT_LRLAT)
                    / (Math.pow(2, depthofImage));

        }

    }
}



