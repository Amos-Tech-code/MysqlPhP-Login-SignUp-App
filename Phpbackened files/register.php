<?php
require_once("db.php");
require_once("helper_classes.php");

// Takes raw data from the request
$json = file_get_contents('php://input');
// Converts it into a PHP object
$data = json_decode($json);

$response = array();
$result = new Result();

if ($data != null) {
    if (!empty($data->name) && !empty($data->email) && !empty($data->password)) {
        $name = $data->name;
        $email = $data->email;
        $password = $data->password;
        $stmt = $conn->prepare("SELECT * FROM users WHERE email=?");
        $stmt->bind_param("s",$email);
        $stmt->execute();
        $stmt->store_result();
        $rows = $stmt->fetch();
        $stmt->close();
        if ($rows == 0) {
            $passEnc = password_hash($password, PASSWORD_DEFAULT);
            $stmt = $conn->prepare("INSERT INTO users(name, email, password) VALUES (?,?,?)");
            $stmt->bind_param("sss", $name, $email, $passEnc);
            if ($stmt->execute()) {
                $result->setErrorStatus(false);
                $result->setMessage("registered successfully, please login");
            } else {
                $result->setErrorStatus(true);
                $result->setMessage("Something went wrong. Please retry");
            }
        } else {
            $result->setErrorStatus(true);
            $result->setMessage("you are already registered, please login");
        }
    } else {
        $result->setErrorStatus(true);
        $a = json_encode($data);
        $result->setMessage("insufficient parameters");
    }
} else {
    $result->setErrorStatus(true);
    $result->setMessage("no data received");
}

$response['error'] = $result->isError();
$response['message'] = $result->getMessage();
echo json_encode($response);
?>