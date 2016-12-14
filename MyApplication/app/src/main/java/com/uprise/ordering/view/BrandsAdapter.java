package com.uprise.ordering.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.uprise.ordering.R;
import com.uprise.ordering.model.BrandModel;
import com.uprise.ordering.model.CartItemsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cicciolina on 12/10/16.
 */

public class BrandsAdapter extends BaseExpandableListAdapter implements ViewPager.OnPageChangeListener {

    private LayoutInflater inflater;
    private ArrayList<BrandModel> mParent;
    private ExpandableListView accordion;
    public int lastExpandedGroupPosition;
    private Context context;
    private ViewPager viewPagerBrandList;
    private ImageButton leftNav;
    private ImageButton rightNav;
    private ProductPagerAdapter.ProductPagerAdapterListener productPagerAdapterListener;
    private List<CartItemsModel> cartItemsModelList;
    private int productListSize;

    private BrandsAdapter.BrandsAdapterListener brandsAdapterListener;

    public BrandsAdapter(Context context, ArrayList<BrandModel> parent, ExpandableListView accordion,
                           ProductPagerAdapter.ProductPagerAdapterListener productPagerAdapterListener,
                           BrandsAdapter.BrandsAdapterListener brandsAdapterListener,
                           List<CartItemsModel> cartItemsModelList) {
        this.mParent = parent;
        this.inflater = LayoutInflater.from(context);
        this.accordion = accordion;
        this.context = context;
        this.brandsAdapterListener = brandsAdapterListener;
        this.cartItemsModelList = cartItemsModelList;
        this.productPagerAdapterListener = productPagerAdapterListener;
    }


    @Override
    //counts the number of group/parent items so the list knows how many times calls getGroupView() method
    public int getGroupCount() {
        return mParent.size();
    }

    @Override
    //counts the number of children items so the list knows how many times calls getChildView() method
    public int getChildrenCount(int i) {
        this.productListSize = mParent.get(i).getProducts().size();
        return 1;
    }

    @Override
    //gets the title of each parent/group
    public Object getGroup(int i) {
        return mParent.get(i).getBrandName();
    }

    @Override
    //gets the name of each item
    public Object
    getChild(int i, int i1) {
        return mParent.get(i).getProducts().get(i1);
    }
    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.shop_now_list_item_products, viewGroup,false);
        }
        // set category name as tag so view can be found view later
        view.setTag(getGroup(i).toString());

        ImageView logo = (ImageView) view.findViewById(R.id.iv_brand_logo);

        if(mParent.get(i).getBrandPhotoUrl() != null && !mParent.get(i).getBrandPhotoUrl().isEmpty()) {
            String replacedBase64 = mParent.get(i).getBrandPhotoUrl().replace("data:image/jpeg;base64,","");
            if(replacedBase64.contains("data:image/png;base64,")) {
                replacedBase64 = replacedBase64.replace("data:image/png;base64,","");
            }
            byte[] decodedString = Base64.decode(replacedBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            logo.setImageBitmap(decodedByte);
        }
//        TextView textView = (TextView) view.findViewById(R.id.list_item_text_product);

        //"i" is the position of the parent/group in the list
//        textView.setText(getGroup(i).toString());

//        TextView sub = (TextView) view.findViewById(R.id.list_item_text_subscriptions);

//        if(mParent.get(i).getSelections().size()>0) {
//            sub.setText(mParent.get(i).getSelections().toString());
//        }
//        else {
//            sub.setText("");
//        }

        //return the entire view
        return view;
    }

    @Override
    //in this method you must set the text to see the children on the list
    public View getChildView(final int i, final int i1, final boolean b, View view, final ViewGroup viewGroup) {

        if (view == null) {
            view = inflater.inflate(R.layout.shop_now_list_item_brands, viewGroup,false);
        }
        viewPagerBrandList = (ViewPager) view.findViewById(R.id.viewpager_brand_list);

        ProductPagerAdapter productPagerAdapter = new ProductPagerAdapter(context, mParent.get(i).getProducts(), cartItemsModelList, productPagerAdapterListener, mParent.get(i).getId());
        productPagerAdapter.notifyDataSetChanged();
        viewPagerBrandList.setAdapter(productPagerAdapter);

        viewPagerBrandList.setOffscreenPageLimit(mParent.get(i).getProducts().size());
        viewPagerBrandList.addOnPageChangeListener(this);
        leftNav = (ImageButton) view.findViewById(R.id.left_nav);
        rightNav = (ImageButton) view.findViewById(R.id.right_nav);

// Images left navigation

        leftNav.setVisibility(View.GONE);
        rightNav.setVisibility(View.VISIBLE);

        leftNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = viewPagerBrandList.getCurrentItem();
                if (tab > 0) {
                    tab--;
                    viewPagerBrandList.setCurrentItem(tab);
                } else if (tab == 0) {
                    viewPagerBrandList.setCurrentItem(tab);
                }

                rightNav.setVisibility(View.VISIBLE);
                if(viewPagerBrandList.getCurrentItem() == 0) {
                    leftNav.setVisibility(View.GONE);
                }

            }
        });

        // Images right navigatin
        rightNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = viewPagerBrandList.getCurrentItem();
                tab++;
                leftNav.setVisibility(View.VISIBLE);
                viewPagerBrandList.setCurrentItem(tab);
                if(viewPagerBrandList.getCurrentItem() == mParent.get(i).getProducts().size() - 1) {
                    rightNav.setVisibility(View.GONE);
                }
            }
        });

        if(brandsAdapterListener.isAddOrSaved()) {
            viewPagerBrandList.setCurrentItem(brandsAdapterListener.pageSaved());
            hideOrShowViewPagerButtons();
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    /**
     * automatically collapse last expanded group
     * @see http://stackoverflow.com/questions/4314777/programmatically-collapse-a-group-in-expandablelistview
     */
    public void onGroupExpanded(int groupPosition) {

        if(groupPosition != lastExpandedGroupPosition){
            accordion.collapseGroup(lastExpandedGroupPosition);
        }

        super.onGroupExpanded(groupPosition);

        lastExpandedGroupPosition = groupPosition;

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        hideOrShowViewPagerButtons();

        brandsAdapterListener.onPageChange(viewPagerBrandList, position);
    }

    @Override
    public void onPageSelected(int position) {
        hideOrShowViewPagerButtons();


    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void hideOrShowViewPagerButtons() {
        leftNav.setVisibility(View.VISIBLE);
        rightNav.setVisibility(View.VISIBLE);

        if(viewPagerBrandList.getCurrentItem() == 0) {
            leftNav.setVisibility(View.GONE);
        }

        if(viewPagerBrandList.getCurrentItem() == productListSize- 1) {
            rightNav.setVisibility(View.GONE);
        }
    }

    public interface BrandsAdapterListener {
        void onPageChange(ViewPager viewPager, int position);
        boolean isAddOrSaved();
        int pageSaved();
    }

}
