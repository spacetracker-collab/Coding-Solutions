import torch
import torch.nn as nn
import matplotlib.pyplot as plt
from pft_model import PreferenceFieldNet

def simulate_drift(steps=20):
    # Initialize the model (The AI's current understanding of the user)
    model = PreferenceFieldNet(input_dim=2)
    optimizer = torch.optim.Adam(model.parameters(), lr=0.1)
    
    # Initial state of the AI
    current_ai_state = torch.tensor([[0.0, 0.0]], requires_grad=True)
    
    # Drift: The "True" human preference is moving in a circle
    history = []
    
    print("Starting Preference Drift Simulation...")
    for t in range(steps):
        # Calculate the moving target (The Drift)
        target_x = 2.0 * torch.cos(torch.tensor(t * 0.3))
        target_y = 2.0 * torch.sin(torch.tensor(t * 0.3))
        true_goal = torch.tensor([[target_x, target_y]])
        
        # 1. Update the Model (AI learns the new drifted preference)
        # In a real scenario, this comes from new 'keywords' or feedback
        for _ in range(5):
            optimizer.zero_grad()
            output = model(true_goal)
            loss = -output.sum() # Maximize preference at the new goal
            loss.backward()
            optimizer.step()
        
        # 2. AI moves toward the drifted goal using the Gradient Decision Principle
        grad = model.get_gradient(current_ai_state)
        with torch.no_grad():
            current_ai_state += 0.5 * grad
            
        history.append(current_ai_state.clone().detach().numpy())
        print(f"Time {t}: Goal at ({target_x:.2f}, {target_y:.2f}), AI moved to {current_ai_state.detach().numpy()}")

    return history

if __name__ == "__main__":
    history = simulate_drift()
    # Plotting the chase
    history = torch.tensor(history).squeeze()
    plt.figure(figsize=(8, 8))
    plt.plot(history[:, 0], history[:, 1], 'g.-', label='AI Alignment Path')
    plt.title("Preference Drift: AI Tracking a Moving Cognitive Target")
    plt.xlabel("X1")
    plt.ylabel("X2")
    plt.legend()
    plt.show()
