export interface Task {
  id?: number;
  title: string;
  description: string;
  status: 'TODO' | 'IN_PROGRESS' | 'IN_REVIEW' | 'BLOCKED' | 'COMPLETED';
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  dueDate: string;
  startDate?: string;
  completedAt?: string;
  
  // For create/update (send IDs)
  projectId?: number;
  assignedToId?: number;
  
  // For display (receive nested objects from backend)
  project?: {
    id: number;
    name: string;
  };
  assignedTo?: {
    id: number;
    username: string;
    email: string;
  };
  createdBy?: {
    id: number;
    username: string;
    email: string;
  };
  
  tags?: any[];
  createdAt?: string;
  updatedAt?: string;
}
