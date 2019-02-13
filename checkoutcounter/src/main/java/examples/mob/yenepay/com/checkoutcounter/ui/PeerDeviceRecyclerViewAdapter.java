package examples.mob.yenepay.com.checkoutcounter.ui;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import examples.mob.yenepay.com.checkoutcounter.R;

public class PeerDeviceRecyclerViewAdapter
        extends RecyclerView.Adapter<PeerDeviceRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "PeerDeviceRecyclerViewA";
    private final OnDeviceSelectedListner mListner;
    private final List<WifiP2pDevice> mValues;
    private final boolean mTwoPane;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WifiP2pDevice item = (WifiP2pDevice) view.getTag();
            mListner.onDeviceSelected(item);
//            if (mTwoPane) {
//                Bundle arguments = new Bundle();
//                arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.id);
//                ItemDetailFragment fragment = new ItemDetailFragment();
//                fragment.setArguments(arguments);
//                mListner.getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.item_detail_container, fragment)
//                        .commit();
//            } else {
//                Context context = view.getContext();
//                Intent intent = new Intent(context, ItemDetailActivity.class);
//                intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.id);
//
//                context.startActivity(intent);
//            }
        }
    };

    public PeerDeviceRecyclerViewAdapter(OnDeviceSelectedListner parent,
                                         List<WifiP2pDevice> items,
                                         boolean twoPane) {
        mValues = items;
        mListner = parent;
        mTwoPane = twoPane;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).deviceName);
        holder.mContentView.setText(getDeviceStatus(mValues.get(position).status));
        holder.mContainer.setOnClickListener(mOnClickListener);
        holder.mContainer.setTag(holder.mItem);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private static String getDeviceStatus(int deviceStatus) {
        Log.d(TAG, "Peer status :" + deviceStatus);
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";

        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        WifiP2pDevice mItem;
        final View mContainer;
        final ImageView mImageView;
        final TextView mIdView;
        final TextView mContentView;

        ViewHolder(View view) {
            super(view);
            mItem = null;
            mContainer = view;
            mIdView = view.findViewById(R.id.id_text);
            mContentView = view.findViewById(R.id.content);
            mImageView = view.findViewById(R.id.img);
        }
    }

    public interface OnDeviceSelectedListner{
        void onDeviceSelected(WifiP2pDevice device);
    }
}
