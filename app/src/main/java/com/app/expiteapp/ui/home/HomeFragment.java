package com.app.expiteapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.app.expiteapp.MainActivity;
import com.app.expiteapp.R;
import com.app.expiteapp.adapters.ProductAdapter;
import com.app.expiteapp.models.LVPItem;
import com.app.expiteapp.models.ListViewProduct;

import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        UpdateProductList();
        return root;
    }

    @Override
    public void onResume() {
        UpdateProductList();
        super.onResume();
    }

    public void UpdateProductList() {
        //Adapter
        List<ListViewProduct> products = ListViewProduct.getList(MainActivity.DB_HELPER.getReadableDatabase());
        List<LVPItem> fullProductList = LVPItem.generateFormProducts(products, getContext());

        ListView pl = (ListView)root.findViewById(R.id.product_list);
        ProductAdapter adapter = new ProductAdapter(getContext(), fullProductList);

        pl.setAdapter(adapter);
    }
}