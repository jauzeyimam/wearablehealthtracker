package ee364h.wearablehealthtracker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.app.FragmentTransaction;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ee364h.wearablehealthtracker.HomePageFragment.OnGraphSelectedListener} interface
 * to handle interaction events.
 * Use the {@link HomePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class HomePageFragment extends Fragment {

    private OnGraphSelectedListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment
     * @return A new instance of fragment HomePageFragment.
     */
    public static HomePageFragment newInstance() {
        HomePageFragment fragment = new HomePageFragment();
        return fragment;
    }
    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_page_layout, container, false);
    }

    public void onStart(){
        super.onStart();

        /******Buttons*******/
        ImageButton pedometer = (ImageButton ) getView().findViewById(R.id.pedometer);
        pedometer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchGraph(GraphType.PEDOMETER);
            }
        });

        ImageButton pulse = (ImageButton) getView().findViewById(R.id.pulse);
        pulse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchGraph(GraphType.PULSE);
            }
        });

        ImageButton bloodOx = (ImageButton) getView().findViewById(R.id.bloodOx);
        bloodOx.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchGraph(GraphType.BLOODOX);
            }
        });

        ImageButton temperature = (ImageButton) getView().findViewById(R.id.temperature);
        temperature.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchGraph(GraphType.TEMPERATURE);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnGraphSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnGraphSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /****LaunchGraph****
     * Launches a graph fragment depending on which button was pressed
     * by notifying the main activity and passing it the graphType
     * @param graphType - type of graph to launch
     */
    public void launchGraph(GraphType graphType){
        mListener.onGraphSelected(graphType);
    }


    /****OnGraphSelectedListener****
     * Tells the activity to open a graphFragment and
     * pass the graphFragment the type of graph based
     * on which button was pressed.
     */
    public interface OnGraphSelectedListener {
        public void onGraphSelected(GraphType graphType);
    }

}
