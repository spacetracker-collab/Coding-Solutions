import torch
import torch.nn as nn

class PreferenceFieldNet(nn.Module):
    def __init__(self, input_dim=2):
        super(PreferenceFieldNet, self).__init__()
        # Modeling the Scalar Preference Field P(x)
        # Using Tanh activations to ensure a smooth, differentiable manifold
        self.field = nn.Sequential(
            nn.Linear(input_dim, 128),
            nn.Tanh(),
            nn.Linear(128, 64),
            nn.Tanh(),
            nn.Linear(64, 1) # Output is the scalar preference intensity
        )

    def forward(self, x):
        return self.field(x)

    def get_decision_gradient(self, x):
        """Implements dx/dt = ∇P(x) as per the Gradient Decision Principle"""
        x.requires_grad_(True)
        p = self.forward(x)
        grad = torch.autograd.grad(p.sum(), x, create_graph=True)[0]
        return grad


   def get_gradient(self, x):
        """
        Implementation of the Gradient Decision Principle (Section 3).
        Calculates ∇P(x), the direction of steepest preference increase.
        """
        if not x.requires_grad:
            x = x.clone().detach().requires_grad_(True)
        
        p = self.forward(x)
        # Calculate the gradient of the scalar field with respect to the input
        grad = torch.autograd.grad(p.sum(), x, create_graph=True)[0]
        return grad
