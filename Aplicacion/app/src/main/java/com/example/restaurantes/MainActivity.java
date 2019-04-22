package com.example.restaurantes;

import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    Fragment fragment_nuevo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        funcionalidadMenu();
    }

    private void funcionalidadMenu() {
        final BottomNavigationView mainNav=findViewById(R.id.main_nav);

        fragment_nuevo = new Fragment_Mapa().newInstance();
        setFragment(fragment_nuevo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorMapa)));

        mainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_mapa:
                        mainNav.setItemBackgroundResource(R.color.colorMapa);
                        fragment_nuevo = new Fragment_Mapa().newInstance();
                        setFragment(fragment_nuevo);
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorMapa)));
                        return true;

                    case R.id.nav_lista:
                        mainNav.setItemBackgroundResource(R.color.colorMapa);
                        fragment_nuevo = new Fragment_Lista().newInstance();
                        setFragment(fragment_nuevo);
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorMapa)));
                        return true;

                    case R.id.nav_busca:
                        mainNav.setItemBackgroundResource(R.color.colorMapa);
                        fragment_nuevo = new Fragment_Busca().newInstance();
                        setFragment(fragment_nuevo);
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorMapa)));
                        return true;

                    case R.id.nav_perfil:
                        mainNav.setItemBackgroundResource(R.color.colorMapa);
                        fragment_nuevo = new Fragment_Perfil().newInstance();
                        setFragment(fragment_nuevo);
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorMapa)));
                        return true;
                    case R.id.nav_configuraciones:
                        mainNav.setItemBackgroundResource(R.color.colorMapa);
                        fragment_nuevo = new Fragment_Acerca_De();
                        setFragment(fragment_nuevo);
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorMapa)));
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void setFragment(android.support.v4.app.Fragment fragment){
        android.support.v4.app.FragmentTransaction fragmentTran = getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment);
        fragmentTran.commitNow();
    }
}
