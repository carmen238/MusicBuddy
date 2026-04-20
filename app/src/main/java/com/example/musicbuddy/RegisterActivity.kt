val register = findViewById<TextView>(R.id.goRegister)

register.setOnClickListener {
    startActivity(Intent(this, RegisterActivity::class.java))
}