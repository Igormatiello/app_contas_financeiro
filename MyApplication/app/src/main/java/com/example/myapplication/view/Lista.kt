package com.example.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.database.DBHandler

class Lista : AppCompatActivity() {

    private lateinit var dbHandler: DBHandler
    private lateinit var textListaTransacoes: TextView
    private lateinit var btnVoltar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lista)
        btnVoltar = findViewById(R.id.btVoltar)
        textListaTransacoes = findViewById(R.id.tVTransacoes)
        dbHandler = DBHandler(this)
        carregarTransacoes()

        btnVoltar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun carregarTransacoes() {
        val transacoes = dbHandler.buscarTransacoes()
        val builder = StringBuilder()

        for (transacao in transacoes) {
            builder.append("Tipo: ${transacao.cre_deb}\n")
            builder.append("Descrição: ${transacao.descricao}\n")
            builder.append("Valor: R$ %.2f\n".format(transacao.valor))
            builder.append("Data: ${transacao.data}\n")
            builder.append("---------------\n")
        }

        if (builder.isEmpty()) {
            builder.append("Atenção: Nenhuma transação registrada!")
        }

        textListaTransacoes.text = builder.toString()
    }
}
