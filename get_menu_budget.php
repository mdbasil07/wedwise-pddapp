<?php
header("Content-Type: application/json");
include('db.php');

$response = array();

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    if (isset($_POST['id'])) {
        $id = $_POST['id'];

        $stmt = $conn->prepare("SELECT * FROM menu_budget WHERE id = ?");
        $stmt->bind_param("i", $id);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $response["success"] = true;
            $response["data"] = $result->fetch_assoc();
        } else {
            $response["success"] = false;
            $response["message"] = "No menu budget found for the given ID.";
        }
        $stmt->close();
    } else {
        $response["success"] = false;
        $response["message"] = "Missing required parameter: id.";
    }
} else {
    $response["success"] = false;
    $response["message"] = "Invalid request method.";
}

echo json_encode($response);
?>
