package br.edu.ifes.firebaseapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifes.firebaseapp.databinding.ActivitySignBinding

import com.google.firebase.auth.FirebaseAuth

class SignActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialize o View Binding
        binding = ActivitySignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicialize o FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Configurar cliques nos botões usando binding
        binding.signButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val confirm = binding.confirmPasswordEditText.text.toString().trim()
            Log.d("SignActivity", "Tentando criar conta: $email")

            if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Log.d("SignActivity", "Campos vazios detectados")
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!confirm.equals(password)) {
                Log.d("SignActivity", "Senhas não coincidem")
                Toast.makeText(this, "Confirme a mesma senha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    Log.d("SignActivity", "createUserWithEmail:complete")
                    if (task.isSuccessful) {
                        Log.d("SignActivity", "createUserWithEmail:success")

                        // Registro bem-sucedido
                        val user = auth.currentUser
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {

                        Log.w("SignActivity", "createUserWithEmail:failure", task.exception)
                        // Falha no registro
                        Toast.makeText(this, "Criação falhou.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("SignActivity", "createUserWithEmail:failure", e)
                    Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                }
                .continueWith {
                    if (!it.isComplete) {
                        Log.e("SignActivity", "createUserWithEmail:timeout")
                        Toast.makeText(this, "Timeout na criação do usuário", Toast.LENGTH_LONG)
                            .show()
                    }
                    it
                }
        }
    }
}
