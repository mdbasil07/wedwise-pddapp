<?php
include('db.php');

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

// Load PHPMailer
require __DIR__ . '/PHPMailer/src/Exception.php';
require __DIR__ . '/PHPMailer/src/PHPMailer.php';
require __DIR__ . '/PHPMailer/src/SMTP.php';

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (!isset($_POST['email']) || empty($_POST['email'])) {
        echo json_encode(["status" => "error", "message" => "Email address is required."]);
        exit;
    }

    $email = trim($_POST['email']);

    // Check if email exists in the database
    $stmt = $conn->prepare("SELECT id FROM signup WHERE email = ?");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($row = $result->fetch_assoc()) {
        // Generate a 4-digit OTP
        $otp = rand(1000, 9999);

        // Store OTP in the database
        $update_stmt = $conn->prepare("UPDATE signup SET otp = ? WHERE email = ?");
        $update_stmt->bind_param("is", $otp, $email);

        if ($update_stmt->execute()) {
            // Initialize PHPMailer
            $mail = new PHPMailer(true);
            try {
                $mail->isSMTP();
                $mail->Host = 'smtp.gmail.com'; // SMTP Server
                $mail->SMTPAuth = true;
                $mail->Username = 'rmohammedbasil83@gmail.com'; // Your Gmail Address
                $mail->Password = 'gbmq mwnv qyag lnfq'; // Gmail App Password (❗ Store securely)
                $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
                $mail->Port = 587;

                // Email setup
                $mail->setFrom('rmohammedbasil83@gmail.com', 'wedwise'); // Sender's Email & Name
                $mail->addAddress($email); // Recipient's Email

                // Email content
                $mail->isHTML(true);
                $mail->Subject = 'Your OTP Code';
                $mail->Body    = "<p>Your OTP for password reset is: <b>$otp</b></p><p>Please do not share this OTP with anyone.</p>";

                // Send email
                if ($mail->send()) {
                    echo json_encode(["status" => "success", "message" => "OTP sent to your email."]);
                } else {
                    echo json_encode(["status" => "error", "message" => "Failed to send OTP email."]);
                }
            } catch (Exception $e) {
                echo json_encode(["status" => "error", "message" => "Mailer Error: " . $mail->ErrorInfo]);
            }
        } else {
            echo json_encode(["status" => "error", "message" => "Error updating OTP in the database."]);
        }
        $update_stmt->close();
    } else {
        echo json_encode(["status" => "error", "message" => "Email not found."]);
    }

    $stmt->close();
    $conn->close();
}
?>
