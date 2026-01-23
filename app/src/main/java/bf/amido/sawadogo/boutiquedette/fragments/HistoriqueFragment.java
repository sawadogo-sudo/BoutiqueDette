package bf.amido.sawadogo.boutiquedette.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import bf.amido.sawadogo.boutiquedette.R;

public class HistoriqueFragment extends Fragment {
    
    public HistoriqueFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_historique, container, false);
    }
}