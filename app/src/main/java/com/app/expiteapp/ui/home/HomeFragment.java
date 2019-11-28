package com.app.expiteapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.app.expiteapp.AddItem;
import com.app.expiteapp.EditProduct;
import com.app.expiteapp.MainActivity;
import com.app.expiteapp.R;
import com.app.expiteapp.adapters.ProductAdapter;
import com.app.expiteapp.models.LVPItem;
import com.app.expiteapp.models.ListViewProduct;
import com.daimajia.swipe.SwipeLayout;

import java.util.List;

public class HomeFragment extends Fragment {

    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        root.clearFocus();
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
        pl.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                LVPItem item = (LVPItem)parent.getItemAtPosition(position);
                if(item.id != -1) {
                    Intent intent = new Intent(getContext(), EditProduct.class);
                    intent.putExtra(EditProduct.EDITITEM_ID, item.id);
                    startActivity(intent);
                }
                return false;
            }
        });
        pl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               LVPItem item =  (LVPItem)parent.getItemAtPosition(position);
               if (item.simpleClickShow == false && item.isHeader == false) {
                   item.simpleClickShow = true;
                   final SwipeLayout swipeLayout = (SwipeLayout) view.findViewById(R.id.swipable_poduct_item);
                   swipeLayout.open(true);
               }
            }
        });

        ProductAdapter adapter = new ProductAdapter(getContext(), fullProductList);

        pl.setAdapter(adapter);
    }
}