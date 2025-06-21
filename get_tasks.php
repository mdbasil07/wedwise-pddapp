<?php
header("Content-Type: application/json");
include('db.php');

$response = array();

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    if (isset($_POST['id'])) {
        $id = $_POST['id'];

        $stmt = $conn->prepare("SELECT * FROM tasks WHERE id = ?");
        $stmt->bind_param("i", $id);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $response["success"] = true;
            $response["task"] = $result->fetch_assoc();
        } else {
            $response["success"] = false;
            $response["message"] = "Task not found.";
        }
        $stmt->close();
    } else {
        $response["success"] = false;
        $response["message"] = "Missing task ID.";
    }
} else {
    $response["success"] = false;
    $response["message"] = "Invalid request method.";
}

echo json_encode($response);
?>
