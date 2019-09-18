package android.gr.katastima;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ProductListAdapter extends ArrayAdapter<Product>{
    Context context;
    ArrayList<Product> data;

    public ProductListAdapter(Context context, ArrayList<Product> data) {
        super(context, 0, data);
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ProductListAdapter.ViewHolder holder;
        if(rowView == null) {
            rowView = android.view.LayoutInflater.from(context).
                    inflate(R.layout.list_item_product, parent, false);
            holder = new ProductListAdapter.ViewHolder(rowView);
            rowView.setTag(holder);
        }
        else {
            holder = (ProductListAdapter.ViewHolder) rowView.getTag();
        }

        Resources res = context.getResources();

        int reqH = (int)Functions.getListItemHeight(context);

        holder.imageView.setImageBitmap(Functions.getScaledBitmap(res,
                res.getIdentifier(data.get(position).image, "drawable", context.getPackageName()),
                reqH, reqH));

        holder.titleView.setText(data.get(position).title);
        holder.detailsView.setText(data.get(position).details);
        holder.priceView.setText(data.get(position).price + res.getString(R.string.currency).substring(3,4));

        return rowView;
    }


    static class ViewHolder {
        public final ImageView imageView;
        public final TextView titleView,
                detailsView,
                priceView;

        public ViewHolder(View view){
            imageView = (ImageView)view.findViewById(R.id.image_product);
            titleView = (TextView)view.findViewById(R.id.product_title);
            detailsView = (TextView) view.findViewById(R.id.product_details);
            priceView = (TextView) view.findViewById(R.id.product_price);
        }
    }
}
