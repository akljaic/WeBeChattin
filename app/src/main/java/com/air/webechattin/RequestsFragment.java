package com.air.webechattin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RequestsFragment extends Fragment {

    View requestsFragmentView;
    RecyclerView mRequestList;

    public RequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestsFragmentView = inflater.inflate(R.layout.fragment_requests, container, false);

        InitializeFields();

        return requestsFragmentView;
    }

    private void InitializeFields() {
        mRequestList = (RecyclerView)requestsFragmentView.findViewById(R.id.chat_requests_list);
        mRequestList.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}