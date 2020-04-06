package com.google.dmac;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdSize;
//import com.google.android.gms.ads.NativeExpressAdView;
import com.google.dmac.R;
import com.google.dmac.MainActivity;
import com.google.dmac.ProductAdapter;
import com.google.dmac.DatabaseHelper;
import com.google.dmac.Utils;

import java.util.ArrayList;

/**
 * Created by PT on 2/9/2017.
 */

public class ProductListFragment extends Fragment implements Account.ItemScanned {

    private RecyclerView mRecyclerView;
    private ProductAdapter mAdapter;
    private SwipeRefreshLayout swipeRefresh;
    ArrayList<Object> productArrayList;
    private RelativeLayout mainLayout , emptyLayout ;
    DatabaseHelper db ;
    Handler mHandler;
    public ProductListFragment(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_prduct_list,container,false);
        mRecyclerView = view.findViewById(R.id.product_list_recycler_view);
        swipeRefresh = view.findViewById(R.id.swipe_refresh_layout);
        mainLayout = view.findViewById(R.id.main_layout);
        emptyLayout = view.findViewById(R.id.empty_layout);
        this.mHandler = new Handler();
        m_Runnable.run();
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.green),getResources().getColor(R.color.blue),getResources().getColor(R.color.orange));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               loadProductList();
                //Toast.makeText(getActivity(), "Go hard!!", Toast.LENGTH_SHORT).show();
            }
        });
       loadProductList();
        return view;
    }

    private void loadProductList() {
        db= new DatabaseHelper(getContext());
        productArrayList = db.getAllProduct();
        if(Utils.isNetworkAvailable(getContext())){
            addNativeExpressAd();
        }

        if(!productArrayList.isEmpty()){
            mAdapter = new ProductAdapter(getContext(), productArrayList);
            mRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mAdapter);
            swipeRefresh.setRefreshing(false);
            emptyLayout.setVisibility(View.GONE);
        }
        else{
            emptyLayout.setVisibility(View.VISIBLE);
            swipeRefresh.setRefreshing(false);
        }

    }

    private void addNativeExpressAd() {
        /** Setting adViewItem dynamically into the **/
      /*  for(int i=0 ; i<productArrayList.size(); i+=4){

            final  NativeExpressAdView adView = new NativeExpressAdView(getActivity());
            adView.setAdUnitId(getContext().getResources().getString(R.string.ad_unit_id));
            adView.setAdSize(new AdSize(320 , 150));
            adView.loadAd(new AdRequest.Builder().build());
            productArrayList.add(i , adView);
        }*/

    }
    private final Runnable m_Runnable = new Runnable()
    {
        public void run()

        {
            //Toast.makeText(MainActivity.this,"in runnable",Toast.LENGTH_SHORT).show();
            //
           // mHandler.postDelayed(m_Runnable,20000);
           /* FragmentTransaction ft = getFragmentManager().beginTransaction();
            if(Build.VERSION.SDK_INT>=26)
            {
                ft.setReorderingAllowed(false);
            }
            ft.detach(ProductListFragment.this).attach(ProductListFragment.this).commit();*/


            loadProductList();

        }

    };


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        loadProductList();
    }

    @Override
    public void itemUpdated() {
        loadProductList();
    }
    @Override
    public void setUserVisibleHint(boolean isvisibletouser)
    {
        super.setUserVisibleHint(isvisibletouser);
        if(isvisibletouser){
              getFragmentManager().beginTransaction().detach(this).attach(this).commit();


        }
    }




}

