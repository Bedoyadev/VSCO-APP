<?php

if($_SERVER['REQUEST_METHOD']=='POST'){
    require_once 'conexion.php';

    $email1 = isset($_POST['email1']) ? $_POST['email1'] : '';
    $nom_perfil = isset($_POST['nom_perfil']) ? $_POST['nom_perfil'] : '';
    $contra2 = isset($_POST['contra2']) ? $_POST['contra2'] : '';
    $cumple1 = isset($_POST['cumple1']) ? $_POST['cumple1'] : '';


    $stmt = $conn->prepare("INSERT INTO usuarios (nom_perfil, cumple1, email1, contra2) VALUES (?, ?, ?, ?)");
    if ($stmt) {
        $stmt->bind_param("ssss", $nom_perfil, $cumple1, $email1, $contra2);
        $resultado = $stmt->execute();
        if($resultado){
            echo "El usuario se inserto de forma exitosa";
        } else {
            echo "Error al insertar el usuario";
        }
        $stmt->close();
    } else {
        echo "Error en la preparación de la consulta";
    }
}