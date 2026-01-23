package bf.amido.sawadogo.boutiquedette.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import bf.amido.sawadogo.boutiquedette.R;
import bf.amido.sawadogo.boutiquedette.ClientsActivity;

public class ClientsFragment extends Fragment {

    private Button btnGestionClients;
    
    public ClientsFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clients, container, false);
        
        btnGestionClients = view.findViewById(R.id.btnGestionClients);
        btnGestionClients.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ClientsActivity.class);
            startActivity(intent);
        });
        
        return view;
    }
}