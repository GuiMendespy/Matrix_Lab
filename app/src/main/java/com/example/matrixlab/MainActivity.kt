package com.example.matrixlab

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import com.example.matrixlab.R
import com.example.matrixlab.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // infla o layout principal e configura a Toolbar como barra superior
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        // botão flutuante
        binding.appBarMain.fab?.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }

        // --- Configuração da Navegação ---

        // Pega o NavHostFragment (container que vai carregar os fragments).
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?)!!
        // Obtém o NavController, que é quem controla a navegação.
        val navController = navHostFragment.navController

        // 1. Configuração do Menu Lateral (Drawer) e da AppBar (Toolbar)
        // Esta é a ÚNICA vez que o AppBarConfiguration deve ser usado com setupActionBarWithNavController.
        binding.navView?.let {
            // Define TODOS os destinos de nível superior.
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_BancoQuestoes, R.id.nav_Estudo, R.id.nav_Simulador, R.id.nav_settings
                ),
                binding.drawerLayout // <-- Vincula ao DrawerLayout
            )
            // Configura a ActionBar para mostrar o botão Hamburger/Voltar.
            setupActionBarWithNavController(navController, appBarConfiguration)
            // Vincula o menu lateral ao NavController.
            it.setupWithNavController(navController)
        }

        // 2. Configuração da Barra de Navegação Inferior (BottomNavView)
        // O Kotlin agora verifica se contentMain existe E se bottomNavView existe
        // 🔑 CORREÇÃO: Adicionar ?. ao contentMain
        binding.appBarMain.contentMain?.bottomNavView?.let { bottomNavView ->
            // Apenas vincula o BottomNavView ao NavController.
            bottomNavView.setupWithNavController(navController)
        }
    }

    // 3 pontinhos
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val result = super.onCreateOptionsMenu(menu)
        // Usando findViewById porque o NavigationView existe em diferentes arquivos de layout
        // entre w600dp e w1240dp
        val navView: NavigationView? = findViewById(R.id.nav_view)
        if (navView == null) {
            // O menu lateral já contém os itens, incluindo os itens no menu de overflow
            // Só inflamos o menu de overflow se o menu lateral não estiver visível
            menuInflater.inflate(R.menu.overflow, menu)
        }
        return result
    }

    // itens do menu, aqui consegue encontrar as configurações
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_settings -> {
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                navController.navigate(R.id.nav_settings)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // navegação do botão voltar, configurada em NavController
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}