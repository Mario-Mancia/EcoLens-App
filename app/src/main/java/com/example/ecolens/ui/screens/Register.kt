/**
 * Pantalla de registro para nuevos usuarios de la aplicación
 */

package com.example.ecolens.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ecolens.R
import com.example.ecolens.data.local.session.SessionViewModel
import com.example.ecolens.ui.viewmodels.RegisterViewModel

/**
 * Función composable para construir la pantalla de registro de nuevos usuarios.
 */
@Composable
fun RegisterScreen(navController: NavHostController, registerViewModel: RegisterViewModel, modifier: Modifier = Modifier) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf("") }

    val registerSuccess by registerViewModel.registerSuccess.collectAsState()
    val errorMessage by registerViewModel.errorMessage.collectAsState()

    val isFormValid = username.isNotBlank() &&
            email.isNotBlank() &&
            selectedCountry.isNotBlank() &&
            selectedGender.isNotBlank() &&
            password.isNotBlank() &&
            confirmPassword.isNotBlank() &&
            password == confirmPassword

    val context = LocalContext.current

    LaunchedEffect(registerSuccess) {
        if (registerSuccess == true) {
            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
            navController.navigate("Login")
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            if (it.isNotEmpty()) {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF409D44),
                        Color(0xFF037A6F),
                        Color(0xFF026B60)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.navigate("Start") }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }
                IconButton(onClick = { /* Más opciones */ }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Opciones",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            Text(
                text = stringResource(id = R.string.registerMsg),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val fieldModifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(stringResource(id = R.string.txtUserName)) },
                    leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                    singleLine = true,
                    modifier = fieldModifier
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(id = R.string.txtEmail)) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = fieldModifier
                )

                Spacer(modifier = Modifier.height(16.dp))

                CountryDropdownMenu(
                    selectedCountry = selectedCountry,
                    onCountrySelected = { selectedCountry = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = fieldModifier) {
                    Text(
                        text = stringResource(id = R.string.gender),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    listOf(
                        stringResource(id = R.string.genderM),
                        stringResource(id = R.string.genderF)
                    ).forEach { gender ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedGender == gender,
                                onClick = { selectedGender = gender }
                            )
                            Text(text = gender)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(id = R.string.txtPassword)) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = fieldModifier
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(stringResource(id = R.string.txtConfPassword)) },
                    leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = fieldModifier
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        registerViewModel.registerUser(
                            username,
                            email,
                            selectedCountry,
                            selectedGender,
                            password,
                            confirmPassword
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF026B60),
                        contentColor = Color.White
                    ),
                    enabled = isFormValid
                ) {
                    Text(
                        text = stringResource(id = R.string.createAccountButton),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(id = R.string.existAcc),
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                TextButton(onClick = { navController.navigate("Login") }) {
                    Text(
                        text = stringResource(id = R.string.loginButton),
                        color = Color(0xFF026B60),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDropdownMenu(
    selectedCountry: String,
    onCountrySelected: (String) -> Unit
) {
    val countries = listOf(
        "El Salvador",
        "Guatemala",
        "Honduras",
        "Nicaragua",
        "Costa Rica",
        "Panamá"
    )
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
    ) {
        TextField(
            readOnly = true,
            value = selectedCountry,
            onValueChange = {},
            label = { Text("País") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .background(color = Color.Transparent)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            countries.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onCountrySelected(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}



@Preview(showSystemUi = true, showBackground = true)
@Composable
fun RegisterPreview() {

}