<?php
    $usuario=$_POST['email1'];
    $contra2=$_POST['contra2'];
    require_once 'conexion.php';

    $sql = "SELECT * FROM usuarios WHERE email1 = '$usuario' AND contra2 = '$contra2'";
    $result = mysqli_query($conn, $sql);

    if (mysqli_num_rows($result) > 0) {
        // Usuario encontrado
        $row = mysqli_fetch_assoc($result);
        echo "Bienvenido " . $row['nombre'];
    } else {
        echo "Credenciales incorrectas";
    }

    mysqli_close($conn);