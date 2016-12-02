<!DOCTYPE html>
<html lang="en">
	<head>
		<?php
			$title = "Index";
			include 'includes/head.php';
		?>
	</head>

	<body>

		<?php
			include 'includes/header.php';
			include 'includes/aside.php';
		?>

		<section class="body">

      <?php
				system("sudo python /var/www/html/scripts/test.py");
				echo "<p>poop</p>";
      ?>
			<!-- HERE IS WHERE THE BODY GOES! -->

		</section>

		<?php include 'includes/footer.php'; ?>

	</body>
</html>
