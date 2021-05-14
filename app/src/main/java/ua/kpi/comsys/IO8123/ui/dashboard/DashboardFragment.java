package ua.kpi.comsys.IO8123.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.llollox.androidtoggleswitch.widgets.ToggleSwitch;
import java.util.ArrayList;
import java.util.List;

import ua.kpi.comsys.IO8123.R;


public class DashboardFragment extends Fragment {
    private static short togglePosition;
    private GraphView coordPlot;
    private PieChart pieChart;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        coordPlot = (GraphView) view.findViewById(R.id.coordPlot);
        pieChart = (PieChart) view.findViewById(R.id.pieChart);
        ToggleSwitch toggleSwitch = (ToggleSwitch) view.findViewById(R.id.toggleGraphs);
        pieChart.setUsePercentValues(true);

        toggleSwitch.setOnChangeListener(position -> {
            if (position == 0) {
                drawPlot();
                togglePosition = 0;
            } else {
                PieDiagram();
                togglePosition = 1;
            }
        });

        toggleSwitch.setCheckedPosition(togglePosition);
        if (togglePosition == 0) {
            drawPlot();
        } else {
            PieDiagram();
        }
    }


    public void drawPlot() {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        pieChart.setVisibility(View.INVISIBLE);
        coordPlot.setVisibility(View.VISIBLE);
        double x, y;
        x = -2*Math.PI;
        int points = 400;

        for (int i = 0; i < points; i++) {
            x += 0.01*Math.PI;
            y = Math.sin(x);
            series.appendData(new DataPoint(x, y), true, points);
        }

        series.setAnimated(true);
        series.setColor(Color.BLUE);
        coordPlot.removeAllSeries();
        coordPlot.addSeries(series);

        coordPlot.getViewport().setXAxisBoundsManual(true);
        coordPlot.getViewport().setMinX(-2*Math.PI);
        coordPlot.getViewport().setMaxX(2*Math.PI);

        coordPlot.getViewport().setYAxisBoundsManual(true);
        coordPlot.getViewport().setMinY(-5);
        coordPlot.getViewport().setMaxY(5);

        coordPlot.getViewport().setScalable(true);
        coordPlot.getViewport().setScalableY(true);

        coordPlot.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        coordPlot.getGridLabelRenderer().setVerticalLabelsVisible(false);
    }

    public void PieDiagram() {
        coordPlot.setVisibility(View.INVISIBLE);
        pieChart.setVisibility(View.VISIBLE);

        List<PieEntry> values = new ArrayList<>();

        values.add(new PieEntry(5f));
        values.add(new PieEntry(5f));
        values.add(new PieEntry(10f));
        values.add(new PieEntry(80f));

        PieDataSet pieDataSet = new PieDataSet(values, "Pie Chart");

        final int[] COLORS = {Color.rgb(150,75,0),
                Color.rgb(0,191,255),
                Color.rgb(255,128,0),
                Color.rgb(0,0,255)};

        ArrayList<Integer> colors = new ArrayList<>();

        for(int i: COLORS) {
            colors.add(i);
        }

        pieDataSet.setColors(colors);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setTransparentCircleRadius(0f);
        pieChart.setHoleRadius(50f);
        pieChart.setRotationAngle(0);
        pieData.setValueTextSize(0);
        pieChart.animateY(2000, Easing.EaseInOutExpo);
    }
}