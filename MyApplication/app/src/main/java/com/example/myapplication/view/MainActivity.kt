package com.example.myapplication.view

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.database.DBHandler
import com.example.myapplication.model.Transaction
import java.util.Calendar


class MainActivity : AppCompatActivity() {

    private lateinit var spinnerCredDeb: Spinner
    private lateinit var spinnerDescricao: Spinner
    private lateinit var btSalvar: Button
    private lateinit var btListar: Button
    private lateinit var btSaldo: Button
    private lateinit var valor: EditText
    private lateinit var data: EditText
    private lateinit var btlimpar:  Button

    private val tipos = arrayOf("Crédito", "Débito")
    private val descricoesCredito = arrayOf("Salário", "Extras")
    private val descricoesDebito = arrayOf("Alimentacao", "Transporte", "Saude","Moradia")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        configuracaoSpinners()
        escutarBotoes()
    }

    private fun initViews() {
        spinnerCredDeb = findViewById(R.id.spCreDeb)
        spinnerDescricao = findViewById(R.id.spDescricao)
        btSalvar = findViewById(R.id.btSalvar)
        btListar = findViewById(R.id.btListar)
        btSaldo = findViewById(R.id.btSaldo)
        btlimpar = findViewById(R.id.btLimpar)
        valor = findViewById(R.id.etValor)
        data = findViewById(R.id.etData)
        data.setOnClickListener {
            showDatePicker()
        }
    }
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            //  formato dd/MM/yyyy
            val formattedDate = "%02d/%02d/%04d".format(selectedDay, selectedMonth + 1, selectedYear)
            data.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun configuracaoSpinners() {
        val adapterTipo = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerCredDeb.adapter = adapterTipo

        val descricoesMap = mapOf(
            "Crédito" to descricoesCredito,
            "Débito" to descricoesDebito
        )

        spinnerCredDeb.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val tipoSelecionado = tipos[position]
                val descricoes = descricoesMap[tipoSelecionado]
                val adapterDescricao = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, descricoes!!)
                adapterDescricao.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerDescricao.adapter = adapterDescricao
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }


    private fun escutarBotoes() {
        btSalvar.setOnClickListener { salvarTransaction() }
        btListar.setOnClickListener { startActivity(Intent(this, Lista::class.java)) }
        btSaldo.setOnClickListener { mostrarSaldo() }
        btlimpar.setOnClickListener{ limparRegistros()}
    }
    private fun salvarTransaction() {
        val tipo = spinnerCredDeb.selectedItem.toString()
        val descricao = spinnerDescricao.selectedItem.toString()
        val valorValue = valor.text.toString().toDoubleOrNull() ?: 0.0
        val dataString = data.text.toString()

        val transaction = Transaction(
            cre_deb = tipo,
            descricao = descricao,
            valor = valorValue,
            data = dataString
        )

        val dbHandler = DBHandler(this)
        val success = dbHandler.adicionarTransacao(transaction)

        mostrarMensagemGenerica(if (success > -1) "$tipo salvo!!!" else "Erro ao salvar!!!")
    }


    private fun mostrarSaldo() {
        val dbHandler = DBHandler(this)
        val saldo = dbHandler.calculaSaldo()

        val saldoStatus = if (saldo >= 0) {
            "Seu saldo é positivo!"
        } else {
            "Atenção! Seu saldo está negativo!"
        }
        val formattedMessage = if (saldo >= 0) {
            "<font color='#4CAF50'>Seu saldo atual é: R$ %.2f</font>".format(saldo)
        } else {
            "<font color='#F44336'>Seu saldo atual é: R$ %.2f</font>".format(saldo)
        }

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Saldo Atual")
            .setMessage("$saldoStatus\n\n$formattedMessage")
            .setPositiveButton("OK", null)
            .show()

        alertDialog.findViewById<TextView>(android.R.id.message)?.let {
            it.setText(Html.fromHtml(formattedMessage))
        }
    }


    private fun mostrarMensagemGenerica(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun limparRegistros() {
        val dbHandler = DBHandler(this)
        dbHandler.limparTable()
        Toast.makeText(this, "Registros limpos", Toast.LENGTH_SHORT).show()
    }


}
