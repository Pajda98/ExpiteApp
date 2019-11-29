package com.app.expiteapp.ui.shopping_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.app.expiteapp.MainActivity;
import com.app.expiteapp.R;
import com.app.expiteapp.adapters.ProductAdapter;
import com.app.expiteapp.adapters.ShoppingListAdapter;
import com.app.expiteapp.models.ShoppingListItem;

import java.util.List;

public class ShoppingListFragment extends Fragment {

    View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        UpdateShoppingList(false);
        setupToolbar();
        return root;
    }

    @Override
    public void onPause() {
        if(getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
        super.onPause();
    }

    public void setupToolbar() {
        //bottom toolbar
        Toolbar toolbar = (Toolbar) root.findViewById(R.id.shopping_list_bottom_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name = root.findViewById(R.id.shopping_list_name);
                if(!name.getText().equals("")){
                    ShoppingListItem item = new ShoppingListItem();
                    item.Name = name.getText().toString();
                    item.Done = 0;
                    item.insert(MainActivity.DB_HELPER.getWritableDatabase());
                    UpdateShoppingList(true);
                    name.setText("");
                }
            }
        });
    }

    public void UpdateShoppingList(boolean moveToEnd) {
        //Adapter
        ListView lv  = (ListView)root.findViewById(R.id.shopping_list);
        List<ShoppingListItem> shoppingList = ShoppingListItem.getList(MainActivity.DB_HELPER.getReadableDatabase());

        ShoppingListAdapter adapter = new ShoppingListAdapter(getContext(), shoppingList);

        if (moveToEnd) {
            lv.post(new Runnable() {
                @Override
                public void run() {
                    // Select the last row so it will scroll into view...
                    lv.setSelection(adapter.getCount() - 1);
                }
            });
        }

        lv.setAdapter(adapter);
    }
}