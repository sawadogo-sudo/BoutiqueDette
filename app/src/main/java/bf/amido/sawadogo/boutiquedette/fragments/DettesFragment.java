package bf.amido.sawadogo.boutiquedette.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import bf.amido.sawadogo.boutiquedette.R;
import bf.amido.sawadogo.boutiquedette.AddEditDetteActivity;
import bf.amido.sawadogo.boutiquedette.models.Dette;
import bf.amido.sawadogo.boutiquedette.api.ApiHelper;
import bf.amido.sawadogo.boutiquedette.adapters.DetteAdapter;

public class DettesFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private DetteAdapter detteAdapter;
    private List<Dette> detteList;
    private ApiHelper apiHelper;
    private TextView tvEmpty;
    private Button btnAddDette;
    
    public DettesFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dettes, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        try {
            // Initialiser avec un contexte valide
            if (getContext() != null) {
                apiHelper = new ApiHelper(getContext());
            }
            
            detteList = new ArrayList<>();
            
            initViews(view);
            setupRecyclerView();
            
            // Charger les dettes après un petit délai
            if (getActivity() != null) {
                getActivity().runOnUiThread(this::loadDettes);
            }
            
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Erreur initialisation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    
    private void initViews(View view) {
        try {
            recyclerView = view.findViewById(R.id.recyclerViewDettes);
            tvEmpty = view.findViewById(R.id.tvEmpty);
            btnAddDette = view.findViewById(R.id.btnAddDette);
            
            if (btnAddDette != null) {
                btnAddDette.setOnClickListener(v -> {
                    try {
                        // CORRECTION : Utiliser AddEditDetteActivity au lieu de AddDetteActivity
                        Intent intent = new Intent(getActivity(), AddEditDetteActivity.class);
                        intent.putExtra("MODE", "ADD");
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Erreur ouverture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Erreur vues: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupRecyclerView() {
        try {
            if (getContext() != null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                detteAdapter = new DetteAdapter(getContext(), detteList);
                recyclerView.setAdapter(detteAdapter);
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Erreur RecyclerView: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void loadDettes() {
        try {
            if (apiHelper == null && getContext() != null) {
                apiHelper = new ApiHelper(getContext());
            }
            
            if (apiHelper == null) {
                if (tvEmpty != null) {
                    tvEmpty.setText("ApiHelper non initialisé");
                    tvEmpty.setVisibility(View.VISIBLE);
                }
                if (recyclerView != null) {
                    recyclerView.setVisibility(View.GONE);
                }
                return;
            }
            
            apiHelper.getAllDettes(new ApiHelper.DataCallback<List<Dette>>() {
                @Override
                public void onSuccess(List<Dette> dettes) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                detteList.clear();
                                if (dettes != null && !dettes.isEmpty()) {
                                    detteList.addAll(dettes);
                                    if (tvEmpty != null) {
                                        tvEmpty.setVisibility(View.GONE);
                                    }
                                    if (recyclerView != null) {
                                        recyclerView.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    if (tvEmpty != null) {
                                        tvEmpty.setText("Aucune dette trouvée");
                                        tvEmpty.setVisibility(View.VISIBLE);
                                    }
                                    if (recyclerView != null) {
                                        recyclerView.setVisibility(View.GONE);
                                    }
                                }
                                if (detteAdapter != null) {
                                    detteAdapter.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "Erreur traitement: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                
                @Override
                public void onError(String error) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                if (tvEmpty != null) {
                                    tvEmpty.setText("Erreur: " + (error != null ? error.substring(0, Math.min(error.length(), 50)) : "Erreur inconnue"));
                                    tvEmpty.setVisibility(View.VISIBLE);
                                }
                                if (recyclerView != null) {
                                    recyclerView.setVisibility(View.GONE);
                                }
                                Toast.makeText(getActivity(), "Erreur API: " + error, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "Erreur affichage: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
            
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Erreur chargement: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            if (tvEmpty != null) {
                tvEmpty.setText("Exception: " + e.getMessage());
                tvEmpty.setVisibility(View.VISIBLE);
            }
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Recharger les données quand le fragment revient au premier plan
        if (getActivity() != null) {
            getActivity().runOnUiThread(this::loadDettes);
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nettoyer les références
        recyclerView = null;
        detteAdapter = null;
        apiHelper = null;
    }
}