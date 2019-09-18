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

public class GiftListAdapter extends ArrayAdapter<Gift> {
    Context context;
    ArrayList<Gift> data;

    public GiftListAdapter(Context context, ArrayList<Gift> data) {
        super(context, 0, data);
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder holder;
        if(rowView == null) {
            rowView = android.view.LayoutInflater.from(context).
                    inflate(R.layout.list_item_gift, parent, false);
            holder = new ViewHolder(rowView);
            rowView.setTag(holder);
        }
        else {
            holder = (ViewHolder) rowView.getTag();
        }

        Resources res = context.getResources();

        int reqH = (int)Functions.getListItemHeight(context);

        holder.imageView.setImageBitmap(Functions.getScaledBitmap(res,
                res.getIdentifier(data.get(position).image, "drawable", context.getPackageName()),
                reqH, reqH));


        holder.titleView.setText(data.get(position).title);
        holder.detailsView.setText(data.get(position).details);
        holder.pointsView.setText(data.get(position).points + " p");

        return rowView;
    }


    static class ViewHolder {
        public final ImageView imageView;
        public final TextView titleView,
            detailsView,
            pointsView;

        public ViewHolder(View view){
            imageView = (ImageView)view.findViewById(R.id.image_gift);
            titleView = (TextView)view.findViewById(R.id.gift_title);
            detailsView = (TextView) view.findViewById(R.id.gift_details);
            pointsView = (TextView) view.findViewById(R.id.gift_points);
        }
    }
}
