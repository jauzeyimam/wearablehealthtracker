package ee364h.wearablehealthtracker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidplot.Plot;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.TextOrientationType;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.xy.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GraphFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GraphFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphFragment extends Fragment {
    protected static final String ARG_GRAPHTYPE = "graphType";
    private GraphType thisGraphType;
    private OnFragmentInteractionListener mListener;
    private final static String TAG = GraphFragment.class.getSimpleName();
    private List<Number> times = new ArrayList<Number>();
    private Number[] data;
    // Create a couple arrays of y-values to plot:
    private Number[] testData1 = {0.998638, 0.998858, 0.989098, 0.969456, 0.940127, 0.901404, 0.853675, 0.797416, 0.733190, 0.661638, 0.583476, 0.499483, 0.410499, 0.317414, 0.221158, 0.122692, 0.023000, -0.076922, -0.176076, -0.273470, -0.368131, -0.459115, -0.545511, -0.626456, -0.701142, -0.768823, -0.828822, -0.880539, -0.923458, -0.957151, -0.981280, -0.995604, -0.999981, -0.994366, -0.978816, -0.953485, -0.918628, -0.874592, -0.821818, -0.760832, -0.692244, -0.616740, -0.535073, -0.448060, -0.356570, -0.261518, -0.163852, -0.064549, 0.035398, 0.134992, 0.233238, 0.329152, 0.421778, 0.510190, 0.593504, 0.670888, 0.741569, 0.804840, 0.860069, 0.906705, 0.944282, 0.972423, 0.990849, 0.999374, 0.997914, 0.986482, 0.965195, 0.934263, 0.893997, 0.844798, 0.787158, 0.721653, 0.648937, 0.569738, 0.484846, 0.395109, 0.301425, 0.204729, 0.105988, 0.006187, -0.093676, -0.192602, -0.289604, -0.383712, -0.473987, -0.559525, -0.639473, -0.713032, -0.779466, -0.838112, -0.888384, -0.929779, -0.961885, -0.984379, -0.997038, -0.999735, -0.992443, -0.975235, -0.948282, -0.911855, -0.866316, -0.812122, -0.749813, -0.680012, -0.603417, -0.520793, -0.432965, -0.340811, -0.245252, -0.147242, -0.047761, 0.052197, 0.151633, 0.249555, 0.344983, 0.436964, 0.524579, 0.606953, 0.683262, 0.752744, 0.814705, 0.868526, 0.913669, 0.949682, 0.976207, 0.992978, 0.999827, 0.996687, 0.983588, 0.960661, 0.928135, 0.886336, 0.835681, 0.776676, 0.709911, 0.636053, 0.555839, 0.470072, 0.379608, 0.285351, 0.188242, 0.089253, -0.010628, -0.110402, -0.209074, -0.305656, -0.399185, -0.488725, -0.573382, -0.652310, -0.724720, -0.789889, -0.847166, -0.895978, -0.935837, -0.966347, -0.987200, -0.998190, -0.999207, -0.990240, -0.971378, -0.942811, -0.904823, -0.857795, -0.802196, -0.738582, -0.667588, -0.589924,0.018590, 0.118313, 0.216854, 0.313229, 0.406474, 0.495657, 0.579888, 0.658325, 0.730184, 0.794747, 0.851370, 0.899486, 0.938614};
    // private Number[] testData2 = {0.089268, 0.188257, 0.285365, 0.379622, 0.470085, 0.555852, 0.636065, 0.709922, 0.776686, 0.835690, 0.886343, 0.928141, 0.960665, 0.983590, 0.996688, 0.999827, 0.992976, 0.976204, 0.949678, 0.913663, 0.868518, 0.814696, 0.752734, 0.683251, 0.606941, 0.524566, 0.436950, 0.344969, 0.249540, 0.151618, 0.052182, -0.047776, -0.147257, -0.245267, -0.340825, -0.432979, -0.520806, -0.603429, -0.680023, -0.749823, -0.812131, -0.866324, -0.911861, -0.948287, -0.975238, -0.992445, -0.999735, -0.997037, -0.984377, -0.961881, -0.929774, -0.888377, -0.838104, -0.779457, -0.713021, -0.639462, -0.559513, -0.473974, -0.383698, -0.289590, -0.192587, -0.093660, 0.006202, 0.106002, 0.204744, 0.301440, 0.395123, 0.484859, 0.569750, 0.648949, 0.721663, 0.787167, 0.844806, 0.894003, 0.934268, 0.965199, 0.986485, 0.997915, 0.999373, 0.990847, 0.972420, 0.944277, 0.906699, 0.860062, 0.804831, 0.741559, 0.670877, 0.593492, 0.510177, 0.421765, 0.329138, 0.233223, 0.134977, 0.035383, -0.064564, -0.163867, -0.261532, -0.356584, -0.448074, -0.535086, -0.616752, -0.692255, -0.760842, -0.821826, -0.874600, -0.918634, -0.953490, -0.978819, -0.994367, -0.999981, -0.995603, -0.981277, -0.957147, -0.923453, -0.880532, -0.828813, -0.768813, -0.701132, -0.626444, -0.545498, -0.459101, -0.368117, -0.273455, -0.176061, -0.076907, 0.023015, 0.122707, 0.221173, 0.317429, 0.410513, 0.499496, 0.583488, 0.661650, 0.733201, 0.797426, 0.853683, 0.901411, 0.940132, 0.969459, 0.989100, 0.998859, 0.998637, 0.988437, 0.968361, 0.938609, 0.899479, 0.851362, 0.794738, 0.730174, 0.658313, 0.579876, 0.495644, 0.406460, 0.313214, 0.216840, 0.118298, 0.018575, -0.081334, -0.180430, -0.277724, -0.372243, -0.463042, -0.549215, -0.629900, -0.704291, -0.771645, -0.831290, -0.882628, -0.925148, -0.958423, -0.982123, -0.996009, -0.999944, -0.993887, -0.977900, -0.952142, -0.916870, -0.872438, -0.819288, -0.757953, -0.689044};
    // private List<Number> series1Numbers = new ArrayList<Number>(Arrays.asList(testData1));
    private List<Number> series1Numbers = new ArrayList<Number>();
    // private List<Number> series2Numbers = new ArrayList<Number>(Arrays.asList(testData2));


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param graphType Type of Graph.
     * @return A new instance of fragment GraphFragment.
     */
    public static GraphFragment newInstance(GraphType graphType) {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GRAPHTYPE, graphType.name());
        fragment.setArguments(args);
        return fragment;
    }

    public GraphFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            thisGraphType = GraphType.valueOf(getArguments().getString(ARG_GRAPHTYPE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.xy_plot, container, false);
    }

    public void onStart(){
        super.onStart();
        Log.d(TAG,"OnStart Started");
        getDataFromFile();
        Log.d(TAG,"GetDataFromFile Called...");

//        TextView textView = (TextView) getView().findViewById(R.id.defaultTextView);
//        textView.append("- " + thisGraphType.name());

        // initialize our XYPlot reference:
        XYPlotZoomPan plot = (XYPlotZoomPan) getView().findViewById(R.id.mainXYPlot);

        /*Border*/
        plot.setPlotMargins(0, 0, 0, 0);
        plot.setPlotPadding(0, 0, 0, 0);
        plot.setGridPadding(0, 10, 10, 0);
        plot.setBorderStyle(XYPlot.BorderStyle.SQUARE, null, null);
        plot.getGraphWidget().setSize(new SizeMetrics(
                0, SizeLayoutType.FILL,
                0, SizeLayoutType.FILL));

        /*Background*/
//        plot.getLayoutManager().setMarkupEnabled(true);
        plot.setBackgroundColor(Color.WHITE);
        plot.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
        plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        plot.getGraphWidget().setMargins(75, 50, 20,75);
        plot.getLayoutManager().moveToBottom(plot.getGraphWidget());

        /*Legend*/
        plot.getLegendWidget().position(0, XLayoutStyle.ABSOLUTE_FROM_RIGHT,250 ,YLayoutStyle.ABSOLUTE_FROM_BOTTOM,AnchorPosition.RIGHT_BOTTOM);
        plot.getLegendWidget().setSize(new SizeMetrics(75,SizeLayoutType.ABSOLUTE,350,SizeLayoutType.ABSOLUTE));
        plot.getLegendWidget().getTextPaint().setTextSize(45);
        plot.getLegendWidget().setIconSizeMetrics(new SizeMetrics(50,SizeLayoutType.ABSOLUTE,50,SizeLayoutType.ABSOLUTE));

        /*Title*/
        plot.getLayoutManager().add(plot.getTitleWidget());
        plot.getLayoutManager().moveToTop(plot.getTitleWidget());
        plot.getTitleWidget().position(0, XLayoutStyle.ABSOLUTE_FROM_CENTER, 0, YLayoutStyle.ABSOLUTE_FROM_TOP, AnchorPosition.TOP_MIDDLE);
        plot.getTitleWidget().setText(thisGraphType.getName());
        plot.getTitleWidget().getLabelPaint().setTextSize(90);
        plot.getTitleWidget().setSize(new SizeMetrics(150, SizeLayoutType.ABSOLUTE, 750, SizeLayoutType.ABSOLUTE));
        plot.getTitleWidget().getLabelPaint().setColor(Color.GRAY);
        plot.getTitleWidget().getLabelPaint().setFakeBoldText(true);

        /*X Axis Title*/
        plot.getLayoutManager().add(plot.getDomainLabelWidget());
        plot.getLayoutManager().moveToTop(plot.getDomainLabelWidget());
        plot.getDomainLabelWidget().position(0, XLayoutStyle.ABSOLUTE_FROM_CENTER, 0, YLayoutStyle.ABSOLUTE_FROM_BOTTOM, AnchorPosition.BOTTOM_MIDDLE);
        plot.getDomainLabelWidget().setText("Time of Day");
        plot.getDomainLabelWidget().getLabelPaint().setTextSize(45);
        plot.getDomainLabelWidget().setSize(new SizeMetrics(65, SizeLayoutType.ABSOLUTE, 350, SizeLayoutType.ABSOLUTE));
        plot.getDomainLabelWidget().getLabelPaint().setColor(Color.GRAY);

        /*Y Axis Title*/
        plot.getLayoutManager().add(plot.getRangeLabelWidget());
        plot.getLayoutManager().moveToTop(plot.getRangeLabelWidget());
        plot.getRangeLabelWidget().position(0, XLayoutStyle.RELATIVE_TO_LEFT, 0, YLayoutStyle.ABSOLUTE_FROM_CENTER, AnchorPosition.LEFT_MIDDLE);
        plot.getRangeLabelWidget().setText(thisGraphType.getUnits());
        plot.getRangeLabelWidget().setOrientation(TextOrientationType.VERTICAL_ASCENDING);
        plot.getRangeLabelWidget().getLabelPaint().setTextSize(45);
        plot.getRangeLabelWidget().setSize(new SizeMetrics(500,SizeLayoutType.ABSOLUTE,75,SizeLayoutType.ABSOLUTE));
        plot.getRangeLabelWidget().getLabelPaint().setColor(Color.GRAY);

        /*Y Axis*/
        plot.getGraphWidget().getDomainLabelPaint().setColor(Color.GRAY);
        plot.getGraphWidget().getDomainLabelPaint().setTextSize((float) 24.0);
        plot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.GRAY);
        plot.getGraphWidget().getDomainGridLinePaint().setColor(Color.TRANSPARENT);
        plot.getGraphWidget().getDomainSubGridLinePaint().setColor(Color.TRANSPARENT);
        plot.setRangeValueFormat(new DecimalFormat("0.0"));
        plot.setRangeBoundaries(0,100,BoundaryMode.FIXED);
        plot.setRangeStep(XYStepMode.SUBDIVIDE, 11);
        // plot.setRangeStepValue(10);
        plot.setTicksPerRangeLabel(1);


        /*X Axis*/
        plot.getGraphWidget().getRangeLabelPaint().setColor(Color.GRAY);
        plot.getGraphWidget().getRangeLabelPaint().setTextSize((float) 24.0);
        plot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.GRAY);
        plot.getGraphWidget().getRangeGridLinePaint().setColor(Color.TRANSPARENT);
        plot.getGraphWidget().getRangeSubGridLinePaint().setColor(Color.TRANSPARENT);
        plot.getGraphWidget().setDomainLabelOrientation(0);


        plot.setDomainValueFormat(new Format() {

            // create a simple date format that draws on the year portion of our timestamp.
            // see http://download.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html
            // for a full description of SimpleDateFormat.
            private SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");

            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {

                // because our timestamps are in seconds and SimpleDateFormat expects milliseconds
                // we multiply our timestamp by 1000:
                long timestamp = ((Number) obj).longValue();
                Date date = new Date(timestamp);
                return dateFormat.format(date, toAppendTo, pos);
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;

            }
        });
//        plot.setDomainStepValue(1);
//        plot.setTicksPerDomainLabel(5);

        // plot.getLayoutManager().remove(plot.getDomainLabelWidget());
        // plot.getLayoutManager().remove(plot.getRangeLabelWidget());
        // plot.getLayoutManager().remove(plot.getLegendWidget());
        // plot.getLayoutManager().remove(plot.getTitleWidget());


        // // Create a couple arrays of y-values to plot:
        // Number[] series1Numbers = {0.998638, 0.998858, 0.989098, 0.969456, 0.940127, 0.901404, 0.853675, 0.797416, 0.733190, 0.661638, 0.583476, 0.499483, 0.410499, 0.317414, 0.221158, 0.122692, 0.023000, -0.076922, -0.176076, -0.273470, -0.368131, -0.459115, -0.545511, -0.626456, -0.701142, -0.768823, -0.828822, -0.880539, -0.923458, -0.957151, -0.981280, -0.995604, -0.999981, -0.994366, -0.978816, -0.953485, -0.918628, -0.874592, -0.821818, -0.760832, -0.692244, -0.616740, -0.535073, -0.448060, -0.356570, -0.261518, -0.163852, -0.064549, 0.035398, 0.134992, 0.233238, 0.329152, 0.421778, 0.510190, 0.593504, 0.670888, 0.741569, 0.804840, 0.860069, 0.906705, 0.944282, 0.972423, 0.990849, 0.999374, 0.997914, 0.986482, 0.965195, 0.934263, 0.893997, 0.844798, 0.787158, 0.721653, 0.648937, 0.569738, 0.484846, 0.395109, 0.301425, 0.204729, 0.105988, 0.006187, -0.093676, -0.192602, -0.289604, -0.383712, -0.473987, -0.559525, -0.639473, -0.713032, -0.779466, -0.838112, -0.888384, -0.929779, -0.961885, -0.984379, -0.997038, -0.999735, -0.992443, -0.975235, -0.948282, -0.911855, -0.866316, -0.812122, -0.749813, -0.680012, -0.603417, -0.520793, -0.432965, -0.340811, -0.245252, -0.147242, -0.047761, 0.052197, 0.151633, 0.249555, 0.344983, 0.436964, 0.524579, 0.606953, 0.683262, 0.752744, 0.814705, 0.868526, 0.913669, 0.949682, 0.976207, 0.992978, 0.999827, 0.996687, 0.983588, 0.960661, 0.928135, 0.886336, 0.835681, 0.776676, 0.709911, 0.636053, 0.555839, 0.470072, 0.379608, 0.285351, 0.188242, 0.089253, -0.010628, -0.110402, -0.209074, -0.305656, -0.399185, -0.488725, -0.573382, -0.652310, -0.724720, -0.789889, -0.847166, -0.895978, -0.935837, -0.966347, -0.987200, -0.998190, -0.999207, -0.990240, -0.971378, -0.942811, -0.904823, -0.857795, -0.802196, -0.738582, -0.667588, -0.589924,0.018590, 0.118313, 0.216854, 0.313229, 0.406474, 0.495657, 0.579888, 0.658325, 0.730184, 0.794747, 0.851370, 0.899486, 0.938614};
        // Number[] series2Numbers = {0.089268, 0.188257, 0.285365, 0.379622, 0.470085, 0.555852, 0.636065, 0.709922, 0.776686, 0.835690, 0.886343, 0.928141, 0.960665, 0.983590, 0.996688, 0.999827, 0.992976, 0.976204, 0.949678, 0.913663, 0.868518, 0.814696, 0.752734, 0.683251, 0.606941, 0.524566, 0.436950, 0.344969, 0.249540, 0.151618, 0.052182, -0.047776, -0.147257, -0.245267, -0.340825, -0.432979, -0.520806, -0.603429, -0.680023, -0.749823, -0.812131, -0.866324, -0.911861, -0.948287, -0.975238, -0.992445, -0.999735, -0.997037, -0.984377, -0.961881, -0.929774, -0.888377, -0.838104, -0.779457, -0.713021, -0.639462, -0.559513, -0.473974, -0.383698, -0.289590, -0.192587, -0.093660, 0.006202, 0.106002, 0.204744, 0.301440, 0.395123, 0.484859, 0.569750, 0.648949, 0.721663, 0.787167, 0.844806, 0.894003, 0.934268, 0.965199, 0.986485, 0.997915, 0.999373, 0.990847, 0.972420, 0.944277, 0.906699, 0.860062, 0.804831, 0.741559, 0.670877, 0.593492, 0.510177, 0.421765, 0.329138, 0.233223, 0.134977, 0.035383, -0.064564, -0.163867, -0.261532, -0.356584, -0.448074, -0.535086, -0.616752, -0.692255, -0.760842, -0.821826, -0.874600, -0.918634, -0.953490, -0.978819, -0.994367, -0.999981, -0.995603, -0.981277, -0.957147, -0.923453, -0.880532, -0.828813, -0.768813, -0.701132, -0.626444, -0.545498, -0.459101, -0.368117, -0.273455, -0.176061, -0.076907, 0.023015, 0.122707, 0.221173, 0.317429, 0.410513, 0.499496, 0.583488, 0.661650, 0.733201, 0.797426, 0.853683, 0.901411, 0.940132, 0.969459, 0.989100, 0.998859, 0.998637, 0.988437, 0.968361, 0.938609, 0.899479, 0.851362, 0.794738, 0.730174, 0.658313, 0.579876, 0.495644, 0.406460, 0.313214, 0.216840, 0.118298, 0.018575, -0.081334, -0.180430, -0.277724, -0.372243, -0.463042, -0.549215, -0.629900, -0.704291, -0.771645, -0.831290, -0.882628, -0.925148, -0.958423, -0.982123, -0.996009, -0.999944, -0.993887, -0.977900, -0.952142, -0.916870, -0.872438, -0.819288, -0.757953, -0.689044};

        // Turn the above arrays into XYSeries':
        XYSeries series1 = new SimpleXYSeries(
                // Arrays.asList(testData1),          // SimpleXYSeries takes a List so turn our array into a List
                times, // Y_VALS_ONLY means use the element index as the x value
                series1Numbers,          // SimpleXYSeries takes a List so turn our array into a List
                thisGraphType.getTitle());                             // Set the display title of the series

        // same as above
        // XYSeries series2 = new SimpleXYSeries(series2Numbers, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");

        // Create a formatter to use for drawing a series using LineAndPointRenderer
        // and configure it from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter();
        series1Format.setPointLabelFormatter(new PointLabelFormatter());
        series1Format.configure(getActivity().getApplicationContext(),
                R.xml.line_point_formatter_with_plf1);

        // add a new series' to the xyplot:
        plot.addSeries(series1, series1Format);

        // same as above:
        // LineAndPointFormatter series2Format = new LineAndPointFormatter();
        // series2Format.setPointLabelFormatter(new PointLabelFormatter());
        // series2Format.configure(getActivity().getApplicationContext(),
        //         R.xml.line_point_formatter_with_plf2);
        // plot.addSeries(series2, series2Format);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void getDataFromFile(){
        String filename = thisGraphType.getFilename();
        try {
            FileInputStream fis = getActivity().openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            int numLines = 0;
            String [] values;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                values = line.split(" ");
                series1Numbers.add(thisGraphType.getValueFromStringArray(values));
                times.add(Long.valueOf(values[0]));
                numLines++;
            }
            Log.d(TAG, "Data From File:\n" + sb.toString() +"\nNumber of lines: " + numLines);
        }catch(FileNotFoundException e)
        {
            Log.d(TAG,"File Not Found: " + filename);
            e.printStackTrace();
        } catch(Exception e){
            Log.d(TAG, "IO Exception reading data from " + filename);
            e.printStackTrace();
        }
    }

}
