let createdOrderId = null;

function showProductManagement() {
    document.getElementById('content-area').innerHTML = `
        <h2>Product management</h2>
        <button onclick="showCreateForm()">Create product</button>
        <button onclick="showUpdateForm()">Update product</button>
        <button onclick="showDeleteForm()">Delete product</button>
        <button onclick="showProductList()">Show all products</button>
    `;
}

function showOrderPage() {
    document.getElementById('content-area').innerHTML = `
        <h2>Make order</h2>
        <div id="product-list"></div>
        <button onclick="submitOrder()">Order</button>
        <div id="order-result"></div>
    `;
    loadOrderSection();
}

function showCreateForm() {
    document.getElementById('content-area').innerHTML = `
        <h2>Create product</h2>
        <form id="create-product-form">
            <input type="text" id="name" placeholder="Name" required><br><br>
            <input type="number" id="quantity" placeholder="Quantity" required><br><br>
            <input type="number" id="price" step="0.01" placeholder="Price" required><br><br>
            <input type="number" id="x" placeholder="Location X" required><br><br>
            <input type="number" id="y" placeholder="Location Y" required><br><br>
            <button type="submit">CREATE</button>
        </form>
        <div id="create-result"></div>
    `;

    document.getElementById('create-product-form').addEventListener('submit', function (e) {
        e.preventDefault();

        const product = {
            name: document.getElementById('name').value,
            quantity: parseInt(document.getElementById('quantity').value),
            price: parseFloat(document.getElementById('price').value),
            location: {
                x: parseInt(document.getElementById('x').value),
                y: parseInt(document.getElementById('y').value)
            }
        };

        fetch('/products', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(product)
        })
        .then(response => {
            if (response.ok) return response.json();
            return response.text().then(errorMsg => { throw new Error(errorMsg); });
        })
        .then(data => {
            document.getElementById('create-result').innerHTML = `<p style="color:green;">Successfully created product!</p>`;
        })
        .catch(error => {
            document.getElementById('create-result').innerHTML = `<p style="color:red;">${error.message}</p>`;
        });
    });
}

function showUpdateForm() {
    document.getElementById('content-area').innerHTML = `
        <h2>Update product</h2>
        <form id="update-product-form">
            <input type="number" id="update-id" placeholder="ID" required><br><br>
            <input type="text" id="update-name" placeholder="Name"><br><br>
            <input type="number" id="update-quantity" placeholder="Quantity"><br><br>
            <input type="number" id="update-price" step="0.01" placeholder="Price"><br><br>
            <input type="number" id="update-x" placeholder="Location X"><br><br>
            <input type="number" id="update-y" placeholder="Location Y"><br><br>
            <button type="submit">UPDATE</button>
        </form>
        <div id="update-result"></div>
    `;

    document.getElementById('update-product-form').addEventListener('submit', function (e) {
        e.preventDefault();

        const id = document.getElementById('update-id').value;

        const updatedProduct = {
            name: document.getElementById('update-name').value,
            quantity: parseInt(document.getElementById('update-quantity').value),
            price: parseFloat(document.getElementById('update-price').value),
            location: {
                x: parseInt(document.getElementById('update-x').value),
                y: parseInt(document.getElementById('update-y').value)
            }
        };

        fetch(`/products/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updatedProduct)
        })
        .then(response => {
            if (response.ok) return response.json();
            else throw new Error("Error updating the product.");
        })
        .then(data => {
            document.getElementById('update-result').innerHTML = `<p style="color:green;">Successfully updated!</p>`;
        })
        .catch(error => {
            document.getElementById('update-result').innerHTML = `<p style="color:red;">${error.message}</p>`;
        });
    });
}

function showDeleteForm() {
    document.getElementById('content-area').innerHTML = `
        <h2>Delete product</h2>
        <form id="delete-product-form">
            <input type="number" id="delete-id" placeholder="Product ID" required><br><br>
            <button type="submit">DELETE</button>
        </form>
        <div id="delete-result"></div>
    `;

    document.getElementById('delete-product-form').addEventListener('submit', function (e) {
        e.preventDefault();

        const id = document.getElementById('delete-id').value;

        fetch(`/products/${id}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                document.getElementById('delete-result').innerHTML = `<p style="color:green;">Successfully deleted product with ID: ${id}</p>`;
            } else {
                throw new Error("Error deleting the product.");
            }
        })
        .catch(error => {
            document.getElementById('delete-result').innerHTML = `<p style="color:red;">${error.message}</p>`;
        });
    });
}

function showProductList() {
    fetch('/products')
        .then(response => response.json())
        .then(products => {
            let html = `<h2>Products</h2>`;
            if (products.length === 0) {
                html += "<p>Products not found.</p>";
            } else {
                html += `
                    <table border="1" cellpadding="5">
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Quantity</th>
                            <th>Price</th>
                            <th>Location (X, Y)</th>
                        </tr>
                `;
                products.forEach(p => {
                    html += `
                        <tr>
                            <td>${p.id}</td>
                            <td>${p.name}</td>
                            <td>${p.quantity}</td>
                            <td>${p.price}</td>
                            <td>(${p.location.x}, ${p.location.y})</td>
                        </tr>
                    `;
                });
                html += `</table>`;
            }
            document.getElementById('content-area').innerHTML = html;
        })
        .catch(err => {
            document.getElementById('content-area').innerHTML = `<p style="color:red;">Error loading products</p>`;
        });
}

function loadOrderSection() {
  fetch('/products')
    .then(res => res.json())
    .then(products => {
      const contentArea = document.getElementById('content-area');
      contentArea.innerHTML = `
        <h2>Create order</h2>
        <div id="product-list"></div>
        <button onclick="submitOrder()">Finish order</button>
        <div id="order-result" style="margin-top: 15px;"></div>
        <div id="track-section" style="display: none; margin-top: 20px;">
            <button onclick="trackBot()">🚗 Show bot route</button>
            <div id="bot-path" style="margin-top: 10px;"></div>
        </div>
      `;

      const listDiv = document.getElementById('product-list');
      let html = `
        <table border="1" cellpadding="5">
          <tr>
            <th>Name</th>
            <th>Price</th>
            <th>Available quantity</th>
            <th>Requested quantity</th>
          </tr>
      `;

      products.forEach(p => {
        html += `
          <tr>
            <td>${p.name}</td>
            <td>${p.price}</td>
            <td>${p.quantity}</td>
            <td><input type="number" min="0" id="order-qty-${p.id}" value="0"></td>
          </tr>
        `;
      });

      html += '</table>';
      listDiv.innerHTML = html;
    });
}

function submitOrder() {
  fetch('/products')
    .then(res => res.json())
    .then(products => {
      const orderItems = [];

      products.forEach(p => {
        const qty = parseInt(document.getElementById(`order-qty-${p.id}`).value);
        if (qty > 0) {
          orderItems.push({ productId: p.id, quantity: qty });
        }
      });

      if (orderItems.length === 0) {
        alert('Choose at least one product.');
        return;
      }

      fetch('/orders', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ items: orderItems })
      })
      .then(res => res.json())
      .then(data => {
        const resultDiv = document.getElementById('order-result');
          if (data.status === "SUCCESS") {
            createdOrderId = data.orderId;
            resultDiv.innerHTML = `
              ✅ ${data.message}<br>
              Order ID: ${data.orderId}
            `;
            document.getElementById('track-section').style.display = 'block';
          } else {
             let missingText = '';
                if (data.missingItems && data.missingItems.length > 0) {
                  missingText = '<strong>Missing products::</strong><ul>';
                  data.missingItems.forEach(item => {
                    missingText += `<li>${item.productName} — requested: ${item.requested}, available: ${item.available}</li>`;
                  });
                  missingText += '</ul>';
                }

                resultDiv.innerHTML = `
                  ❌ ${data.message}<br>
                  ${missingText}
                `;
                document.getElementById('track-section').style.display = 'none';
        }
      });
    });
}

function trackBot() {
    if (!createdOrderId) return;

    fetch(`/routes?orderId=${createdOrderId}`)
        .then(res => res.json())
        .then(data => {
            const pathDisplay = document.getElementById('bot-path');
            if (data.visitedLocations && data.visitedLocations.length > 0) {
                const locations = data.visitedLocations.map(loc => `(${loc[0]}, ${loc[1]})`).join(' ➡️ ');
                pathDisplay.innerHTML = `<strong>Route:</strong><br>${locations}`;
            } else {
                pathDisplay.innerHTML = "❌ Route not found.";
            }
        })
        .catch(err => {
            document.getElementById('bot-path').innerHTML = "⚠️ Error loading the route.";
        });
}

