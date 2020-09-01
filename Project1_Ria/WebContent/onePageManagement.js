(function() { // avoid variables ending up in the global scope

	// page components
	var AlbumsList, ImageList, ImageDetails, NextButton, PreviousButton, CommentForm, RedirectToIndex, CloseModalWindow;
	pageOrchestrator = new PageOrchestrator(); // main controller

	window.addEventListener("load", () => {
		pageOrchestrator.start(); // initialize the components
		pageOrchestrator.refresh();
	}, false);


	// Constructors of view components



	function AlbumsList(_alert, _albumsRow, _albumsBody) {
		this.alert = _alert;
		this.albumsRow = _albumsRow;
		this.albumsBody = _albumsBody;

		this.reset = function() {
			this.albumsRow.style.visibility = "hidden";
		}

		this.show = function(next) {
			var that = this;
			makeCall("GET", "GetAlbumList", null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var albumsToShow = JSON.parse(req.responseText);
							console.log(req.responseText);
							console.log(albumsToShow);
							if (albumsToShow.length == 0) {
								that.alert.textContent = "No albums available!";
								return;
							}
							that.update(albumsToShow); // that visible by closure
							if (next) next(); // show the default element of the list if present
						}
					} else {
						that.alert.textContent = message;
					}
				}
			);
		};


		this.update = function(arrayAlbums) {
			var elem, i, row, imageCell, imageTag, titleCell, linkTitle, dateCell, dateBody, titleAnchor;
			this.albumsBody.innerHTML = ""; // empty the table body
			// build updated list
			var that = this;
			arrayAlbums.forEach(function(album) {

				row = document.createElement("tr");

				imageCell = document.createElement("td");
				imageTag = document.createElement("img");
				//imageTag.currentSrc = album.iconPath;
				imageTag.setAttribute('src', album.iconPath);
				imageTag.classList.add("img-thumbnail");
				imageCell.appendChild(imageTag);
				row.appendChild(imageCell);

				titleCell = document.createElement("td");
				titleAnchor = document.createElement("a");
				linkTitle = document.createTextNode(album.title);
				titleAnchor.appendChild(linkTitle);
				titleCell.appendChild(titleAnchor);
				row.appendChild(titleCell);

				titleAnchor.setAttribute('albumId', album.id);
				titleAnchor.addEventListener("click", (e) => {
					e.preventDefault();
					imageList.show(e.target.getAttribute("albumId"));
					imageList.resetButtonsNextAndPrevious();
					imageList.resetImages();
					imageDetails.reset();
					commentForm.reset();
				}, false);
				titleAnchor.href = "#";

				dateCell = document.createElement("td");
				dateBody = document.createElement("p");
				dateBody = document.createTextNode(album.date);
				dateCell.appendChild(dateBody);
				row.appendChild(dateCell);

				that.albumsBody.appendChild(row);

			});
			this.albumsRow.style.visibility = "visible";

		}



	}


	function ImageList(_alert, _galleryRow, _galleryBody, _previousButton, _nextButton) {
		this.alert = _alert;
		this.galleryRow = _galleryRow;
		this.galleryBody = _galleryBody;
		this.previousButton = _previousButton;
		this.nextButton = _nextButton;
		this.images = null;
		this.currentBlock = 0;
		this.numToShow = 5;

		this.reset = function() {
			this.galleryRow.style.visibility = "hidden";
		}

		this.resetImages = function() {
			this.images = null;
			this.currentBlock = 0;
			this.numToShow = 5;
		}

		this.resetButtonsNextAndPrevious = function() {
			this.previousButton.classList.remove("visible");
			this.previousButton.classList.add("invisible");
			this.nextButton.classList.remove("visible");
			this.nextButton.classList.add("invisible");
		}


		this.show = function(albumId) {
			var that = this;

			makeCall("GET", "GetImagesOfAlbum?albumId=" + albumId, null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var imagesToShow = JSON.parse(req.responseText);
							if (imagesToShow.length == 0) {
								that.alert.textContent = "No images of this album available!";
								return;
							}
							that.update(imagesToShow); // that visible by closure
						}
					} else {
						that.alert.textContent = message;
					}
				}
			);
		};


		this.update = function(arrayImages) {
			var row, imageThumbnail, titleBody, imageAnchor, imageTag;
			this.galleryBody.innerHTML = ""; // empty the table body
			// build updated list
			var that = this;
			row = document.createElement("tr");


			if (arrayImages) {
				that.images = arrayImages;
			}
			var i = that.currentBlock * that.numToShow;

			var end = i + that.numToShow;
			if (end > that.images.length) {
				end = that.images.length;
			}


			if (end < that.images.length && that.currentBlock > 0) {
				that.nextButton.classList.remove("invisible");
				that.nextButton.classList.add("visible");
				that.previousButton.classList.remove("invisible");
				that.previousButton.classList.add("visible");
			} else {
				if (end >= that.images.length && that.currentBlock > 0) {
					that.nextButton.classList.remove("visible");
					that.nextButton.classList.add("invisible");
					that.previousButton.classList.remove("invisible");
					that.previousButton.classList.add("visible");
				}
				if (end < that.images.length && that.currentBlock === 0) {
					that.nextButton.classList.remove("invisible");
					that.nextButton.classList.add("visible");
					that.previousButton.classList.remove("visible");
					that.previousButton.classList.add("invisible");
				}
			}


			for (; i < end; i++) {

				var image = that.images[i];
				imageThumbnail = document.createElement("td");
				titleBody = document.createElement("p");
				titleBody.textContent = image.title;
				imageThumbnail.appendChild(titleBody);
				imageAnchor = document.createElement("a");
				imageTag = document.createElement("img");
				imageTag.setAttribute('src', image.filePath);
				imageTag.classList.add("image");
				imageTag.classList.add("img-thumbnail");
				imageAnchor.appendChild(imageTag);
				imageThumbnail.appendChild(imageAnchor);
				row.appendChild(imageThumbnail);

				imageAnchor.setAttribute('imageId', image.id);
				imageAnchor.addEventListener("mouseenter", (e) => {
					e.preventDefault();
					document.getElementById("box").style.display = 'block';
					imageDetails.show(e.currentTarget.getAttribute("imageId"));
				}, false);
				//imageAnchor.addEventListener("click", (e) => {
				//	e.preventDefault();
				//	imageDetails.show(e.currentTarget.getAttribute("imageId"));
				//}, false);
				imageAnchor.href = "#";


			}
			that.galleryBody.appendChild(row);

			this.galleryRow.style.visibility = "visible";

		}

		this.next = function() {

			if (this.images) {
				if (((this.currentBlock * this.numToShow) + this.numToShow) < this.images.length) {
					this.currentBlock++;
					this.update(null);

				}
			}

		}

		this.previous = function() {

			if (this.images && this.currentBlock > 0) {
				this.currentBlock--;
				this.update(null);

			}
		}

	}

	function NextButton() {
		this.registerEvents = function(orchestrator) {
			document.getElementById("id_nextButton").addEventListener('click', (e) => {
				e.preventDefault();
				orchestrator.moveImagesNext();

			});
		}
	}

	function PreviousButton() {
		this.registerEvents = function(orchestrator) {
			document.getElementById("id_previousButton").addEventListener('click', (e) => {
				e.preventDefault();
				orchestrator.moveImagesPrevious();

			});

		}
	}



	function ImageDetails(_alert, _titleRow, _titleBody, _descriptionBody, _commentRow,
		_commentBody, _imageAndCommentsRow, _imageContainer) {
		this.alert = _alert;
		this.titleRow = _titleRow;
		this.titleBody = _titleBody;
		this.descriptionBody = _descriptionBody;
		this.commentRow = _commentRow;
		this.commentBody = _commentBody;
		this.imageAndCommentsRow = _imageAndCommentsRow;
		this.imageContainer = _imageContainer;


		this.reset = function() {

			this.imageContainer.classList.remove("visible");
			this.imageContainer.classList.add("invisible");
			this.commentBody.innerHTML = "";
			this.commentRow.classList.remove("visible");
			this.commentRow.classList.add("invisible");
			this.descriptionBody.innerHTML = "";
			this.titleBody.innerHTML = "";


		}


		this.show = function(imageId) {
			var that = this;
			makeCall("GET", "GetImageAndComments?imageId=" + imageId, null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var imageAndComments = JSON.parse(req.responseText);
							if (imageAndComments.length == 0) {
								that.alert.textContent = "No images of this album available!";
								return;
							}
							that.update(imageAndComments); // that visible by closure
						}
					} else {
						that.alert.textContent = message;
					}
				}
			);
		};

		this.addNewComment = function(comment) {

			var commentRow, commentCell1, commentUsername, commentCell2, commentP1,
				commentText, commentMedia, commentCell3, commentDate;
			var that = this;

			commentRow = document.createElement("div");
			commentRow.className = "comments-list";
			commentMedia = document.createElement("div");
			commentMedia.className = "media";


			//comments of the image selected
			commentCell1 = document.createElement("div");
			commentP1 = document.createElement("p");
			commentP1.className = "media-heading user_name";
			commentUsername = document.createTextNode(comment.username);
			commentP1.appendChild(commentUsername);
			commentCell1.appendChild(commentP1);
			commentMedia.appendChild(commentCell1);

			commentCell2 = document.createElement("div");
			commentCell2.className = "media-body";
			commentText = document.createTextNode(comment.text);
			commentCell2.appendChild(commentText);
			commentMedia.appendChild(commentCell2);

			commentCell3 = document.createElement("div");
			commentCell3.className = "pull-right";
			commentDate = document.createTextNode(comment.date);
			commentCell3.appendChild(commentDate);
			commentMedia.appendChild(commentCell3);


			commentRow.appendChild(commentMedia);
			that.commentBody.appendChild(commentRow);
		}

		this.showComments = function() {
			var that = this;
			if (that.commentRow.classList.contains("invisible")) {
				that.commentRow.classList.remove("invisible");
				that.commentRow.classList.add("visible");
			}

		}

		this.update = function(imageAndComments) {
			var row1, imageP, dateP, descriptionP, imageCell, imageDateCell, imageTag,
				imageDescription;

			this.titleBody.innerHTML = "";
			this.commentBody.innerHTML = "";
			this.descriptionBody.innerHTML = "";
			// build updated list
			var that = this;
			var image = imageAndComments.image;
			var comments = imageAndComments.comments;

			//passo l'image id come paramentro alla funzione show del comment form.
			commentForm.show(image.id);


			this.imageContainer.classList.remove("invisible");
			this.imageContainer.classList.add("visible");
			if (comments.length > 0) {
				that.commentRow.classList.remove("invisible");
				that.commentRow.classList.add("visible");
			} else {
				this.commentRow.classList.remove("visible");
				this.commentRow.classList.add("invisible");

			}


			//image info : title + date
			imageP = document.createElement("p");
			imageP.className = "h1 pt-3 border-top";
			imageCell = document.createTextNode(image.title);
			imageP.appendChild(imageCell);
			that.titleBody.appendChild(imageP);
			dateP = document.createElement("p");
			dateP.className = "h6";
			imageDateCell = document.createTextNode(image.date);
			dateP.appendChild(imageDateCell);
			that.titleBody.appendChild(dateP);

			//image scr + description
			imageTag = document.createElement("img");
			imageTag.setAttribute('src', image.filePath);
			imageTag.className = "shadow rounded-lg mb-4 image img-fluid";
			that.descriptionBody.appendChild(imageTag);
			descriptionP = document.createElement("p");
			if (image.description) {
				descriptionP.className = "shadow border border-success rounded-pill p-2 text-break visible";
				imageDescription = document.createTextNode(image.description);
				descriptionP.appendChild(imageDescription);
			} else {
				descriptionP.classList.remove("visible");
				descriptionP.classList.add("invisible");
			}

			that.descriptionBody.appendChild(descriptionP);


			comments.forEach(function(comment) {
				that.addNewComment(comment);
			});


		}



	}

	function CloseModalWindow() {

		var modal_close = document.getElementsByClassName("modal_close")[0];
		var box = document.getElementById('box');
		this.registerEvents = function() {
			//chiude la finestra cliccando la x
			modal_close.addEventListener('click', (e) => {
				e.preventDefault();
				box.style.display = "none";
			}, false);

			// Chiude la finestra quando l'utente clicca al di fuori di essa
			window.addEventListener('mouseenter', (e) => {
				e.preventDefault();
				if (e.target == box) { box.style.display = "none"; }
			}, false);
		}
		


	}

	function RedirectToIndex() {

		this.registerEvents = function() {

			document.getElementById("id_loginToComment").addEventListener('click', (e) => {
				e.preventDefault();
				window.location.href = "index.html";
			}, false);
		};

	}

	function CommentForm(_alert, _commentRow, _userNotLogged, _userLogged) {
		this.alert = _alert;
		this.commentRow = _commentRow;
		this.userNotLogged = _userNotLogged;
		this.userLogged = _userLogged;

		this.reset = function() {
			document.getElementById("id_textComment").innerHTML = "";
			this.userNotLogged.classList.remove("visible");
			this.userNotLogged.classList.add("invisible");
			this.userLogged.classList.remove("visible");
			this.userLogged.classList.add("invisible");
			this.commentRow.classList.remove("visible");
			this.commentRow.classList.add("invisible");

		};

		this.show = function(imageId) {
			this.commentRow.classList.remove("invisible");
			this.commentRow.classList.add("visible");
			if (sessionStorage.getItem('username') === null) {
				this.userNotLogged.classList.remove("invisible")
				this.userNotLogged.classList.add("visible")
				this.userLogged.classList.remove("visible")
				this.userLogged.classList.add("invisible")
			} else {
				this.userNotLogged.classList.remove("visible")
				this.userNotLogged.classList.add("invisible")
				this.userLogged.classList.remove("invisible")
				this.userLogged.classList.add("visible")
				document.getElementById("id_commentImageId").value = imageId;
			}


		};

		this.registerEvents = function(orchestrator) {

			document.getElementById("id_commentButton").addEventListener('click', (e) => {
				e.preventDefault();
				var form = e.target.closest("form");
				console.log(form);
				if (form.checkValidity()) {
					makeCall("POST", 'CreateComment', e.target.closest("form"),
						function(req) {
							if (req.readyState == XMLHttpRequest.DONE) {
								var message = req.responseText;

								switch (req.status) {
									case 200:
										var comment = JSON.parse(req.responseText);
										document.getElementById("message").textContent
											= "Comment saved";
										orchestrator.addNewComment(comment);
										break;
									case 400: // bad request
										document.getElementById("message").textContent = message;
										break;
									case 401: // unauthorized
										document.getElementById("message").textContent = message;
										break;
									case 500: // server error
										document.getElementById("message").textContent = message;
										break;
								}
							}
						}
					);
				} else {
					form.reportValidity();
				}
			});
		}

	};




	function PageOrchestrator() {
		var alertContainer = document.getElementById("id_alert");
		this.start = function() {

			albumsList = new AlbumsList(
				alertContainer,
				document.getElementById("id_albumsRow"),
				document.getElementById("id_albumsBody"));

			imageList = new ImageList(
				alertContainer,
				document.getElementById("id_galleryRow"),
				document.getElementById("id_galleryBody"),
				document.getElementById("id_previousButton"),
				document.getElementById("id_nextButton"));

			imageDetails = new ImageDetails(
				alertContainer,
				document.getElementById("id_titleRow"),
				document.getElementById("id_titleBody"),
				document.getElementById("id_descriptionBody"),
				document.getElementById("id_commentRow"),
				document.getElementById("id_commentBody"),
				document.getElementById("id_imageAndCommentsRow"),
				document.getElementById("id_imageContainer"));


			commentForm = new CommentForm(
				alertContainer,
				document.getElementById("id_commentRow"),
				document.getElementById("id_userNotLogged"),
				document.getElementById("id_userLogged"));

			redirectToIndex = new RedirectToIndex();

			closeModalWindow = new CloseModalWindow();

			nextButton = new NextButton();
			previousButton = new PreviousButton();
			nextButton.registerEvents(this);
			previousButton.registerEvents(this);
			commentForm.registerEvents(this);
			redirectToIndex.registerEvents();
			closeModalWindow.registerEvents();

			document.querySelector("a[href='Logout']").addEventListener('click', () => {
				e.preventDefault();
				window.sessionStorage.removeItem('username');
			})
		};



		this.refresh = function() {
			alertContainer.textContent = "";
			albumsList.reset();
			imageList.reset();
			albumsList.show();

		};

		this.addNewComment = function(comment) {
			imageDetails.addNewComment(comment);
			imageDetails.showComments();
		};

		this.resetImageContainer = function() {

			imageDetails.reset();
		};

		this.moveImagesNext = function() {

			imageList.next();
		};

		this.moveImagesPrevious = function() {

			imageList.previous();
		};
	}
})();
