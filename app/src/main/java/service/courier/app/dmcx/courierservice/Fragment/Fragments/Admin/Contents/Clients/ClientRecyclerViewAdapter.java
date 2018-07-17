package service.courier.app.dmcx.courierservice.Fragment.Fragments.Admin.Contents.Clients;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import service.courier.app.dmcx.courierservice.Models.Client;
import service.courier.app.dmcx.courierservice.R;

public class ClientRecyclerViewAdapter extends RecyclerView.Adapter<ClientRecyclerViewAdapter.ClientRecyclerViewHolder> {

    private List<Client> clients;

    public ClientRecyclerViewAdapter(List<Client> clients) {
        this.clients = clients;
    }

    @Override
    public ClientRecyclerViewAdapter.ClientRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_view_admin_single_client, parent, false);
        return new ClientRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClientRecyclerViewAdapter.ClientRecyclerViewHolder holder, int position) {
        holder.clientNameTV.setText(clients.get(position).getName());
        holder.clientStatusTV.setText(clients.get(position).getStatus());

    }

    @Override
    public int getItemCount() {
        return clients.size();
    }

    public class ClientRecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView clientNameTV;
        public TextView clientStatusTV;
        public CircleImageView clientImageCIV;

        public ClientRecyclerViewHolder(View itemView) {
            super(itemView);

            clientNameTV = itemView.findViewById(R.id.clientNameTV);
            clientStatusTV = itemView.findViewById(R.id.clientStatusTV);
            clientImageCIV = itemView.findViewById(R.id.clientImageCIV);
        }
    }

}
