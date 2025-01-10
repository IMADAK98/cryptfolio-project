import {
  trigger,
  transition,
  style,
  animate,
  group,
  query,
} from '@angular/animations';

export const slideInAnimation = trigger('routeAnimations', [
  transition('* <=> *', [
    // Initial state of new route
    query(':enter, :leave', style({ position: 'fixed', width: '100%' }), {
      optional: true,
    }),
    group([
      // Animation for the new route
      query(
        ':enter',
        [
          style({ transform: 'translateX(100%)' }),
          animate('0.5s ease-in-out', style({ transform: 'translateX(0%)' })),
        ],
        { optional: true },
      ),
      // Animation for the old route
      query(
        ':leave',
        [
          style({ transform: 'translateX(0%)' }),
          animate(
            '0.5s ease-in-out',
            style({ transform: 'translateX(-100%)' }),
          ),
        ],
        { optional: true },
      ),
    ]),
  ]),
]);
