package info.androidhive.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

class PerformanceAdapter extends RecyclerView.Adapter<PerformanceAdapter.MyViewHolder> {
    private static RecyclerTouchListener.ClickListener clickListener;

    private List<Performance> moviesList;
    Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            genre = (TextView) view.findViewById(R.id.genre);
            year = (TextView) view.findViewById(R.id.year);
            title.setOnClickListener(this);
            year.setOnClickListener(this);
            genre.setOnClickListener(this);
        }
        public void onClick(View v) {
            // TODO Auto-generated method stub

            if(v == title)
            {
                Toast.makeText(context, "Visiting Card Clicked is ==>"+title.getText(), Toast.LENGTH_SHORT).show();
            }

            if(v == year)
            {
                Toast.makeText(context, "Name ==>"+year.getText(), Toast.LENGTH_SHORT).show();
            }

            if(v == genre)
            {
                Toast.makeText(context, "Email ==>"+genre.getText(), Toast.LENGTH_SHORT).show();
            }
        }
}

    public PerformanceAdapter(List<Performance> moviesList, Context context) {
        this.moviesList = moviesList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Performance movie = moviesList.get(position);
        holder.title.setText(movie.getTitle());
        holder.genre.setText(movie.getGenre());
        holder.year.setText(movie.getYear());
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
