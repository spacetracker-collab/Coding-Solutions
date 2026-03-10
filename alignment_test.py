import torch
from pft_model import PreferenceFieldNet

def simulate_alignment():
    # 1. Initialize the Field (The Human Interest)
    model = PreferenceFieldNet(input_dim=2)
    
    # 2. Define the 'Pin Factory' state (A dangerous trajectory)
    # Let's say [2, 2] is the Pin Factory, but [0, 0] is the Human Goal
    current_state = torch.tensor([[1.5, 1.5]], requires_grad=True)
    
    print(f"Initial AI Trajectory heading toward Pin Factory: {current_state.detach().numpy()}")
    
    # 3. Apply Gradient Decision Principle over 5 steps
    for i in range(5):
        grad = model.get_gradient(current_state)
        # Move along the gradient of the Preference Field
        with torch.no_grad():
            current_state += 0.5 * grad 
        print(f"Step {i+1}: AI adjusting position based on PFT Alignment: {current_state.detach().numpy()}")

if __name__ == "__main__":
    simulate_alignment()
