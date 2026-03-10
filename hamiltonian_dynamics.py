import torch

def get_hamiltonian_flow(model, x, p):
    """
    Implements Hamilton's Equations (Eq 17):
    dx/dt = dH/dp
    dp/dt = -dH/dx
    """
    x.requires_grad_(True)
    p.requires_grad_(True)
    
    # Kinetic Energy K = 0.5 * p^2
    # Potential Energy V = -P(x) (Negative of the Preference Field)
    pref_intensity = model(x)
    H = 0.5 * torch.sum(p**2) - pref_intensity.sum()
    
    # Hamilton's Equations
    dx_dt = torch.autograd.grad(H, p, create_grad_hex=True)[0]
    dp_dt = -torch.autograd.grad(H, x, create_grad_hex=True)[0]
    
    return dx_dt, dp_dt

def simulate_phase_space(model, steps=50, dt=0.1):
    x = torch.tensor([[1.0, 1.0]], requires_grad=True) # Initial State
    p = torch.tensor([[0.1, -0.1]], requires_grad=True) # Initial Momentum (Intent)
    
    trajectory = []
    for _ in range(steps):
        dx, dp = get_hamiltonian_flow(model, x, p)
        with torch.no_grad():
            x += dx * dt
            p += dp * dt
        trajectory.append(x.clone().detach().numpy())
    return trajectory
