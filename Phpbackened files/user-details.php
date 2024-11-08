<?php
require_once("db.php");
require_once("helper_classes.php");

//Array for response data
$response = array();
$result = new Result();

if (!empty($_POST['email']) && !empty($_POST['token'])) {
    $email = $_POST['email'];
    $token = $_POST['token'];

    $stmt = $conn->prepare("SELECT password FROM users WHERE email=?");
    $stmt->bind_param("s",$email);
    $stmt->execute();
    $stmt->store_result();
    $stmt->bind_result($dbPass);
    $stmt->fetch();
    $rows = $stmt->num_rows;
    $stmt->close();
    if($rows>0) {
        $stmt = $conn->prepare("SELECT token FROM users WHERE email=?");
        $stmt->bind_param("s",$email);
        $stmt->execute();
        $stmt->store_result();
        $stmt->bind_result($savedToken);
        $stmt->fetch();
        $stmt->close();
        if ($savedToken == $token) {
            $stmt = $conn->prepare("SELECT name FROM users WHERE email=?");
            $stmt->bind_param("s",$email);
            $stmt->execute();
            $stmt->store_result();
            $stmt->bind_result($name);
            $stmt->fetch();
            $stmt->close();

            $result->setErrorStatus(false);
            $result->setMessage("Retrieval Successful");
            $response['user']['name'] = $name;
        } else {
            $result->setErrorStatus(true);
            $result->setMessage("Invalid token");
        }
    } else {
        $result->setErrorStatus(true);
        $result->setMessage("Invalid credentials");
    }
} else {
    $result->setErrorStatus(true);
    $result->setMessage("insufficient parameters");
}

$response['result']['error'] = $result->isError();
$response['result']['message'] = $result->getMessage();

echo json_encode($response);
?>