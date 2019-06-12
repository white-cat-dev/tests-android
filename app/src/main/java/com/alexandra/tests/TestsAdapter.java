package com.alexandra.tests;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


public class TestsAdapter extends BaseAdapter {

    ArrayList<Test> tests;
    LayoutInflater lInflater;
    TestsActivity context;

    TestsAdapter(TestsActivity context, ArrayList<Test> tests) {
        this.context = context;
        this.tests = tests;

        lInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return tests.size();
    }

    @Override
    public Object getItem(int position) {
        return tests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = lInflater.inflate(R.layout.test_item, parent, false);
        }

        Test test = (Test)getItem(position);

        ((TextView)itemView.findViewById(R.id.title)).setText(test.title);
        ((TextView)itemView.findViewById(R.id.description)).setText(test.description);

        if (test.result != null) {
            ((Button)itemView.findViewById(R.id.button)).setText("Посмотреть ответы");
            String result = test.result.rightAnswers + "/" + test.questionsCount;
            ((TextView)itemView.findViewById(R.id.result)).setText(result);
        }

        (itemView.findViewById(R.id.button)).setOnClickListener(view -> {
            context.loadTest(test.id);
        });

        return itemView;
    }
}